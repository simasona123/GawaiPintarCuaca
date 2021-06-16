package com.example.gpc1.receiver;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.gpc1.Preferences;
import com.example.gpc1.SendData;
import com.example.gpc1.WorkerClass;


public class ReceiverPerubahanJaringan extends BroadcastReceiver {
    private static final int JOB_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Receiver Perubahan Jaringan");
        Log.i("RecPecJar", intent.getAction());
        createScheduler(context);
    }

    private void createScheduler (Context context){
//        Constraints constraints = new Constraints.Builder().
//                setRequiredNetworkType(NetworkType.CONNECTED).build();
//        WorkRequest myWorkRequest = new OneTimeWorkRequest.
//                Builder(WorkerClass.class).setConstraints(constraints).build(); //TODO untuk pengujian workmanager
//        WorkManager.getInstance().enqueueUniqueWork("Worker Pengiriman Data",
//                ExistingWorkPolicy.REPLACE, (OneTimeWorkRequest) myWorkRequest);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            JobScheduler jobScheduler;
            jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            ComponentName serviceName = new ComponentName(context.getPackageName(), SendData.class.getName());
            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName);
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            JobInfo jobInfo = builder.build();
            int resultCode = jobScheduler.schedule(jobInfo);
            if(resultCode <= 0){
                System.out.println("Job Scheduler Gagal");
            }
            else{
                System.out.println("Job Scheduler Berhasil");
            }
        }
        else {
        Constraints constraints = new Constraints.Builder().
                setRequiredNetworkType(NetworkType.CONNECTED).build();
        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(WorkerClass.class).
                setConstraints(constraints).build();
        WorkManager.getInstance().enqueueUniqueWork("Worker Pengiriman Data",
                ExistingWorkPolicy.REPLACE, (OneTimeWorkRequest) myWorkRequest);
        }
    }
}
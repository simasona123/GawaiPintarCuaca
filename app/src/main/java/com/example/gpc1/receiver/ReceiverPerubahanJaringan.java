package com.example.gpc1.receiver;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.gpc1.Constants;
import com.example.gpc1.Preferences;
import com.example.gpc1.SendData;
import com.example.gpc1.WorkerClass;
import com.example.gpc1.background.PengirimanDataService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ReceiverPerubahanJaringan extends BroadcastReceiver {
    private SharedPreferences sharedPreferences;
    private boolean online;
    private static final int JOB_ID = 0;

    @Override //Inisializasi Berada pada PengirimanDataService
    public void onReceive(Context context, Intent intent) {
        System.out.println("Receiver Perubahan Jaringan");
        Log.i("RecPecJar", intent.getAction());
        sharedPreferences = context.getSharedPreferences(Preferences.SHARED_PRE_FILE, Context.MODE_PRIVATE);
        int i = sharedPreferences.getInt(Preferences.SCHEDULING_COUNT, 0);
        if (i > 0 ) {
            createScheduler(context);
        }
    }

    private void createScheduler (Context context){
//        Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
//        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(WorkerClass.class).setConstraints(constraints).build();
//        WorkManager.getInstance().enqueue(myWorkRequest);
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
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(WorkerClass.class).setConstraints(constraints).build();
            WorkManager.getInstance().enqueue(myWorkRequest);
        }
    }
}
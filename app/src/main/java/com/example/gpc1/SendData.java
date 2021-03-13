package com.example.gpc1;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class SendData extends JobService {
    NotificationManager notificationManager;
    NotificationGPC notificationGPC;
    DataModel dataModel;
    DatabaseHelper databaseHelper;
    ArrayList<DataModel> unsendingData = new ArrayList<>();
    boolean jobCancelled;

    @Override
    public boolean onStartJob(JobParameters params) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationGPC = new NotificationGPC(this, notificationManager);
        notificationGPC.deliverNotification("Pengiriman Data");
        databaseHelper = new DatabaseHelper(this);
        doBackgroundWork(params, this);
        return true;
    }

    private void doBackgroundWork(JobParameters params, Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unsendingData = databaseHelper.getUnsendingData();
                ArrayList<String> data = new ArrayList<>();
                for (int i = 0 ; i <unsendingData.size(); i++){
                    data.add(unsendingData.get(i).toJSON(context));
                }
                jobFinished(params, true);
                System.out.println("Data = " + data);
            }
        }).start();
    }
//TODO Volley API, JSON Sudah bisa
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }
}

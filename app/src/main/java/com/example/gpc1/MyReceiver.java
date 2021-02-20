package com.example.gpc1;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyReceiver extends BroadcastReceiver {
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "gpcNotification";
    private final int NOTIFICATION_ID = 0;
    private NotificationGPC notificationGPC;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentService = new Intent(context, IntentServicePerekamanData.class);
        context.startService(intentService);
    }

}
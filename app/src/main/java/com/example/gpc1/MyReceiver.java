package com.example.gpc1;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MyReceiver extends BroadcastReceiver{
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "gpcNotification";
    private final int NOTIFICATION_ID = 0;
    private NotificationGPC notificationGPC;
    Context context;
    private int baterai;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        baterai = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        Intent intentService = new Intent(context, IntentServicePerekamanData.class);
        intentService.putExtra("statusBaterai", baterai);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
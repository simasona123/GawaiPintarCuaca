package com.example.gpc1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;


public class MyReceiver extends BroadcastReceiver{
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int baterai = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        Intent intentService = new Intent(context, IntentServicePerekamanData.class);
        intentService.putExtra("statusBaterai", baterai);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
package com.example.gpc1;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;

import com.example.gpc1.background.IntentServicePerekamanData;
import com.example.gpc1.menus.SensorActivity;

import java.util.Calendar;


public class MyReceiver extends BroadcastReceiver{
    Context context;
    SensorActivity sensorActivity;
    Calendar calendar;
    long milis;
    long x;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Intent intent1 = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int baterai = intent1.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        System.out.println("Receiver Baterai " + baterai);
        Intent intentService = new Intent(context, IntentServicePerekamanData.class);
        intentService.putExtra("statusBaterai", baterai);
        startAlarm(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
    private void startAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        milis = calendar.getTimeInMillis();
        x = milis % (1000 * 60 * Constants.PERIODE_REKAMAN_MENIT);
        Intent notifyIntent = new Intent(context, MyReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (1000 * 60 * Constants.PERIODE_REKAMAN_MENIT - x), notifyPendingIntent);
    }
}
package com.example.gpc1.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.example.gpc1.Constants;
import com.example.gpc1.background.PengirimanDataService;
import com.example.gpc1.Preferences;

import java.util.Calendar;

public class ReceiverPengirimanData extends BroadcastReceiver {
    long milis;
    SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentPengirimanData = new Intent(context, PengirimanDataService.class);
        sharedPreferences = context.getSharedPreferences(Preferences.SHARED_PRE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Jaringan", 1);
        editor.apply();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentPengirimanData);
        } else {
            context.startService(intentPengirimanData);
        }
        createJobScheduler(context);
    }

    private void createJobScheduler(Context context) {
        System.out.println("ReceiverPengirimanData");
        Intent sendingDataIntent = new Intent(context, ReceiverPengirimanData.class);
        PendingIntent sendingDataPendingIntent = PendingIntent.getBroadcast(context, 0, sendingDataIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        milis = calendar.getTimeInMillis();
        int userMaks = sharedPreferences.getInt(Preferences.USER_MAKS, 0);
        int userID = sharedPreferences.getInt(Preferences.ID_USER, 0);
        if (userMaks != 0) {
            // milis = alarm(userMaks, userID, calendar, milis);
            milis = tes(milis);
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, milis, sendingDataPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, milis, sendingDataPendingIntent);
            }
        }
        else {
            milis = tes(milis);
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, milis, sendingDataPendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, milis, sendingDataPendingIntent);
            }
        }
    }
    private long alarm(int userMaks, int userID, Calendar calendar, long milis){
        System.out.println("IntentService => Memulai Pengiriman Data Tidak Awal");
        System.out.println("User Maks = " + sharedPreferences.getInt(Preferences.USER_MAKS,0));
        System.out.println("User ID = " + sharedPreferences.getInt(Preferences.ID_USER,0));
        int n = (userMaks / 25 + 1) * 24;
        float t = ((float)24 / n ) * userID;
        float totalMenit = t * 60;
        int jam = (int) totalMenit / 60;
        int menit = (int) totalMenit % 60;
        System.out.println(t);
        System.out.println(jam);
        System.out.println(menit);
        if (calendar.get(Calendar.HOUR_OF_DAY) >= jam) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        calendar.set(Calendar.HOUR_OF_DAY, jam);
        calendar.set(Calendar.MINUTE, menit);
        calendar.set(Calendar.SECOND, 0);
        milis = calendar.getTimeInMillis();
        System.out.print("Alarm => ");
        System.out.print(", " + calendar.get(Calendar.DAY_OF_YEAR));
        System.out.print(" or " + calendar.get(Calendar.DATE));
        System.out.print(", " + calendar.get(Calendar.HOUR_OF_DAY));
        System.out.println(", " + calendar.get(Calendar.MINUTE));
        return milis;
    }
    private long tes(long milis){
        long x = milis % (60 * 1000 * (Constants.PERIODE_PENGIRIMAN_DATA));
        milis  = milis + (60 * 1000 * (Constants.PERIODE_PENGIRIMAN_DATA)) - x;
        return milis;
    }
}
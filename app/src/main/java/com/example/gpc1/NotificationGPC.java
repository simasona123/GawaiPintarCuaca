package com.example.gpc1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class NotificationGPC {

    private Context context;
    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "gpcNotification";
    private final int NOTIFICATION_ID = 0;

    public NotificationGPC(Context context, NotificationManager notificationManager) {
        this.context = context;
        this.notificationManager = notificationManager;
    }

    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Perekaman Data Otomatis Selesai", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.enableLights(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setDescription("Notification GPC");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder (String contentText){
        Intent notificationIntent = new Intent(context, SensorActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("GPC").setContentText(contentText).setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(notificationPendingIntent).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW);
    }
    public Notification notification (Context context, String contentText){
        createNotificationChannel();
        Intent notificationIntent = new Intent(context, SensorActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("GPC").setContentText(contentText).setSmallIcon(R.drawable.ic_notif)
                .setContentIntent(notificationPendingIntent).setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_LOW).build();
        return notification;
    }
    public void deliverNotification (String contextText){
        createNotificationChannel();
        NotificationCompat.Builder builder = getNotificationBuilder(contextText);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public void cancelNotification (){
        notificationManager.cancelAll();
    }
}
package com.example.gpc1;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class IntentServicePerekamanData extends IntentService {

    private NotificationManager notificationManager;
    private final String NOTIFICATION_CHANNEL_ID = "gpcNotification";
    private final int NOTIFICATION_ID = 0;
    private NotificationGPC notificationGPC;

    private double longitude;
    private double latitude;

    public IntentServicePerekamanData (){
        super("IntenServicePerekamanData");
    }

    @Override
    public void onStart(@Nullable Intent intent, int startId) {
        super.onStart(intent, startId);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationGPC = new NotificationGPC(this, notificationManager);
            notificationGPC.deliverNotification("Akses Lokasi Diperlukan Pada Setting Aplikasi");
        }
        else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    System.out.println("Perekaman Data = " + longitude + " And "  + latitude);
                }
            });
            try{
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationGPC = new NotificationGPC(this, notificationManager);
            notificationGPC.deliverNotification("Perekaman data Berhasil");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

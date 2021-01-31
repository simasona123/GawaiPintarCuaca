package com.example.gpc1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;

public class LocationTask extends AsyncTask <Void, Void, String> {

    String latitude;
    String longitude;
    String lokasi;
    String timeStamp;
    Location lokasiMain;

    private final WeakReference <TextView> mTextView;
    FusedLocationProviderClient fusedLocationProviderClient;


    public LocationTask(FusedLocationProviderClient fusedLocationProviderClient, TextView tv) {
        this.mTextView = new WeakReference<>(tv);
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(Void... voids) {
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(Location location) {
                lokasiMain = location;
                latitude = String.format("%.4f",location.getLatitude());
                longitude = String.format("%.4f",location.getLongitude());
                timeStamp = String.valueOf(location.getTime());
                lokasi = "Latitude: " + latitude + ", Longitude: " + longitude + ", Timestamp: " + timeStamp;
                System.out.println("A = " + lokasi);
            }
        });
        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("B = "+ lokasi);
        return lokasi;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mTextView.get().setText(s);
    }
}

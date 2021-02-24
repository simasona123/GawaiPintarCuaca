package com.example.gpc1;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationTask extends AsyncTask <Void, Void, String> {

    private final String TAG =  LocationTask.class.getSimpleName();
    String latitude;
    String longitude;
    String lokasi;
    String timeStamp;
    Location lokasiMain;
    String kabupaten;
    String provinsi;
    String altitude;

    private final WeakReference <Context> context;
    private final WeakReference <TextView> mTextView;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    public interface AsyncResponse {
        void processFinish(String kabupaten, String provinsi);
    }

    public AsyncResponse listener = null;

    public LocationTask(AsyncResponse listener, Context context1, Geocoder geocoder1, FusedLocationProviderClient fusedLocationProviderClient, TextView tv) {
        this.mTextView = new WeakReference<>(tv);
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.geocoder = geocoder1;
        this.context = new WeakReference<>(context1);
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(Void... voids) {
        lokasi = "Harap Nyalakan GPS anda....";
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(Location location) {
                lokasiMain = location;
                latitude = String.format("%.4f",location.getLatitude());
                longitude = String.format("%.4f",location.getLongitude());
                altitude = String.valueOf(location.getAltitude());
                List <Address> addresses = null;
                String resultMesage = "";
                try{
                    addresses = geocoder.getFromLocation(
                            lokasiMain.getLatitude(), /// Null pointer
                            lokasiMain.getLongitude(),
                            1);
                }
                catch (IOException | NullPointerException e){
                    resultMesage = "Service Not Available";
                    Log.e(TAG, resultMesage, e);
                }
                catch (IllegalArgumentException illegalArgumentException){
                    resultMesage = "Invalid Coordinates Supplied";
                    Log.e(TAG, resultMesage, illegalArgumentException);
                }
                if (addresses == null || addresses.size() == 0){
                    if (resultMesage.isEmpty()){
                        resultMesage = "Address Not Found";
                        Log.e(TAG, resultMesage);
                        lokasi = resultMesage;
                    }
                }
                else{
                    Address address = addresses.get(0);
                    ArrayList <String> addressParts = new ArrayList<>();
                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                        addressParts.add(address.getAddressLine(i));
                    }
                    resultMesage = TextUtils.join("\n", addressParts);
                    kabupaten = address.getSubAdminArea();
                    provinsi = address.getAdminArea();
                    Log.e(TAG, resultMesage);
                    lokasi = kabupaten + ", " + provinsi;
                }
            }
        });
        try{
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        return lokasi;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        mTextView.get().setText(s);
        listener.processFinish(kabupaten, provinsi);
    }
}

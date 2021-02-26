package com.example.gpc1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LocationTask extends AsyncTask <Void, Void, String> {

    private final String TAG =  LocationTask.class.getSimpleName();
    String latitude;
    String longitude;
    String lokasi;
    String timeStamp;
    String kabupaten;
    String provinsi;
    String altitude;
    int x;

    private final Context context;
    private final WeakReference <TextView> mTextView;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    private LocationCallback locationCallback;
    Location location1;
    String resultMesage = "";
    List <Address> addresses = null;

    public interface AsyncResponse {
        void processFinish(String kabupaten, String provinsi);
    }

    public AsyncResponse listener = null;

    public LocationTask(AsyncResponse listener, Context context1, Geocoder geocoder1, FusedLocationProviderClient fusedLocationProviderClient, TextView tv) {
        this.mTextView = new WeakReference<>(tv);
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.geocoder = geocoder1;
        this.context = context1;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(Void... voids) {
        x = 0;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    x = 1;
                    latitude = String.format("%.4f",location.getLatitude());
                    longitude = String.format("%.4f",location.getLongitude());
                    altitude = String.valueOf(location.getAltitude());
                    location1 = location;
                    System.out.println("LocationFused "+ location);
                }
                else {
                    locationManager.requestLocationUpdates(bestProvider, 1000, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(@NonNull Location location2) {
                            locationManager.removeUpdates(this);
                            System.out.println("LocationManager" + location2);
                            location1 = location2;
                        }
                    });
                }
            }
        });
        System.out.println("x = " + x);
        try{
            Thread.sleep(2 * 1000);
            System.out.println("Tidur 1 detik");
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        if (x==0){
            try{
                System.out.println("Tidur 10 detik");
                Thread.sleep(7 * 1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        try{
            addresses = geocoder.getFromLocation(
                    location1.getLatitude(),
                    location1.getLongitude(),
                    1);gi
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
            System.out.println("alamat lengkap " + resultMesage);
            kabupaten = address.getSubAdminArea();
            provinsi = address.getAdminArea();
            String local = address.getLocality();
            String sublocal = address.getSubLocality();
            System.out.println( "Subadmin = " + kabupaten + " Admin Area = " + provinsi + " local = " + local + " sub local = " + sublocal);
            Log.e(TAG, resultMesage);
            lokasi = kabupaten + ", " + provinsi;
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

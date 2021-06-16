package com.example.gpc1.background;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;


import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class LocationTask extends AsyncTask <Void, Void, String> {

    private final String TAG =  LocationTask.class.getSimpleName();
    private String latitude;
    private String longitude;
    private String lokasi;
    private String kabupaten;
    private String provinsi;
    private String altitude;
    private String local;

    @SuppressLint("StaticFieldLeak")
    private final Context context;
    private final WeakReference <TextView> mTextView;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    Location location1;
    String resultMesage = "";
    List <Address> addresses = null;

    public interface AsyncResponse {
        void processFinish(String kabupaten, String provinsi, String local, String s);
    }

    public AsyncResponse listener;

    public LocationTask(AsyncResponse listener, Context context1, Geocoder geocoder1,
                        FusedLocationProviderClient fusedLocationProviderClient, TextView tv) {
        this.mTextView = new WeakReference<>(tv);
        this.fusedLocationProviderClient = fusedLocationProviderClient;
        this.geocoder = geocoder1;
        this.context = context1;
        this.listener = listener;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected String doInBackground(Void... voids) {
        lokasi = "Tidak ada Lokasi";
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    latitude = String.format("%.4f",location.getLatitude());
                    longitude = String.format("%.4f",location.getLongitude());
                    altitude = String.valueOf(location.getAltitude());
                    location1 = location;
                    lokasi = "lat; long; -> " + latitude + "; " + longitude;
                }
            }
        });
        try{
            Thread.sleep(2 * 1000);
        }
        catch (InterruptedException e){
            Log.e(TAG, e.toString());
        }
        if (location1 == null){
            return "Lokasi Tidak Dapat Ditemukan, Silahkan Keluar dan Kembali Beberapa Saat Lagi";
        }
        else{
            try{
                addresses = geocoder.getFromLocation(
                        location1.getLatitude(),
                        location1.getLongitude(),
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
                System.out.println("alamat lengkap " + resultMesage);
                kabupaten = address.getSubAdminArea();
                provinsi = address.getAdminArea();
                local = address.getLocality();
                String sublocal = address.getSubLocality();
                System.out.println( "Subadmin = " + kabupaten + "; Admin Area = " + provinsi + "; local = " + local + "; sub local = " + sublocal);
                lokasi = kabupaten + ", " + provinsi;
            }
        }
        System.out.println(lokasi);
        return lokasi;
    }

    @Override
    protected void onPostExecute(String s) {
        System.out.println(lokasi + " final");
        super.onPostExecute(s);
        listener.processFinish(kabupaten, provinsi, local, s);
    }
}

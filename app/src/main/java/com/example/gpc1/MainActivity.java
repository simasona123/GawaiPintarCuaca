package com.example.gpc1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.util.Locale;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, LocationTask.AsyncResponse {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    String latitude;
    String longitude;
    String timeStamp;
    String lokasi;
    String provinsi;
    String kabupaten;


    private TextView lokasiMainTextView;
    Location lokasiMain;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG,"On Create");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        lokasiMainTextView = findViewById(R.id.lokasi);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        lokasiMainTextView.setText("Mencari.....");
        locationAccessPermission();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this, Locale.getDefault());
        new LocationTask(this,this, geocoder, fusedLocationProviderClient, lokasiMainTextView).execute();

        if (savedInstanceState != null){
            lokasiMainTextView.setText(savedInstanceState.getString("Lokasi Terakhir"));
        }

    }

    @Override
    public void processFinish(String kabupaten, String provinsi) {
        String[] kab =kabupaten.split(" ");
        kabupaten = "";
        for (int i = 1; i < kab.length ; i++){
            kabupaten += kab[i];
            if(i != kab.length -1){
                kabupaten += " ";
            }
        }
        this.kabupaten = kabupaten;
        this.provinsi = provinsi;
        Log.e(LOG_TAG, kabupaten + provinsi);
        new XMLParsingTask(kabupaten, provinsi).execute();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Lokasi Terakhir", lokasiMainTextView.getText().toString());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION :
                if (grantResults.length > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    locationAccessPermission();
                }
                else{
                    Toast.makeText(this, R.string.location_permission_denied,Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void locationAccessPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else {
            Log.d(LOG_TAG, "Lokasi Permisi Diberikan");
        }
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.page_1:
                return true;
            case R.id.page_2:
                Intent intent1 = new Intent (this, SensorActivity.class);
                startActivity(intent1);
                break;
            case R.id.page_3:
                Intent intent2 = new Intent (this, AboutUs.class);
                startActivity(intent2);
                break;
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"On Start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG,"On ReStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"On Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"On Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"On Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,"On Destroy");
    }


}

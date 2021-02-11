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
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, LocationTask.AsyncResponse, XMLParsingTask.XMLParsingTaskResponses {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    String latitude;
    String longitude;
    String timeStamp;
    String lokasi;
    String provinsi;
    String kabupaten;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM HH:mm");


    private TextView lokasiMainTextView;

    private TextView waktu;
    private TextView waktu1;
    private TextView waktu2;
    private TextView suhu;
    private TextView suhu1;
    private TextView suhu2;
    private TextView kelembaban;
    private TextView kelembaban1;
    private TextView kelembaban2;
    private ImageView cuacaIcon;
    private ImageView cuacaIcon2;
    private ImageView cuacaIcon1;

    Location lokasiMain;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG,"On Create");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        lokasiMainTextView = findViewById(R.id.lokasi);
        waktu = findViewById(R.id.waktu);
        waktu1 = findViewById(R.id.waktu1);
        waktu2 = findViewById(R.id.waktu2);
        suhu = findViewById(R.id.suhu);
        suhu1 = findViewById(R.id.suhu1);
        suhu2 = findViewById(R.id.suhu2);
        kelembaban = findViewById(R.id.kelembaban);
        kelembaban1 = findViewById(R.id.kelembaban1);
        kelembaban2 = findViewById(R.id.kelembaban2);
        cuacaIcon = findViewById(R.id.cuacaIcon);
        cuacaIcon1 = findViewById(R.id.cuacaIcon1);
        cuacaIcon2 =  findViewById(R.id.cuacaIcon2);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        lokasiMainTextView.setText("Mencari.....");
        waktu.setText("Mencari.....");
        waktu1.setText("Mencari.....");
        waktu2.setText("Mencari.....");
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
        new XMLParsingTask(this ,kabupaten, provinsi).execute();
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


    @SuppressLint("SetTextI18n")
    @Override
    public void processFinish(ArrayList<Integer> kelembabanParsing, ArrayList<Float> suhuParsing, ArrayList<Integer> kodeCuaca, ArrayList<Date> waktuCuaca) {
        System.out.println(kelembabanParsing);
        System.out.println(suhuParsing);
        System.out.println(kodeCuaca);
        System.out.println(waktuCuaca);
        String waktuText = simpleDateFormat.format(waktuCuaca.get(0));
        String waktu1Text = simpleDateFormat1.format(waktuCuaca.get(1));
        String waktu2Text = simpleDateFormat1.format(waktuCuaca.get(2));
        System.out.println(waktuText+ " " + waktu1Text + " " + waktu2Text);
        waktu.setText(waktuText);
        waktu1.setText(waktu1Text);
        waktu2.setText(waktu2Text);
        suhu.setText(suhuParsing.get(0).toString());
        suhu1.setText(suhuParsing.get(2).toString());
        suhu2.setText(suhuParsing.get(4).toString());
        kelembaban.setText(kelembabanParsing.get(0).toString());
        kelembaban1.setText(kelembabanParsing.get(1).toString());
        kelembaban2.setText(kelembabanParsing.get(2).toString());
        gambarCuaca(cuacaIcon, kodeCuaca.get(0));
        gambarCuaca(cuacaIcon1, kodeCuaca.get(1));
        gambarCuaca(cuacaIcon2, kodeCuaca.get(2));
    }

    private void gambarCuaca(ImageView cuacaIcon2, Integer integer) {
        switch (integer){
            case 0 :
            case 100:
                cuacaIcon2.setImageResource(R.drawable.ic_sun);
                break;
            case 1 :
            case 101:
            case 2 :
            case 102:
                cuacaIcon2.setImageResource(R.drawable.ic_sun_cloudy);
                break;
            case 3:
            case 103:
                cuacaIcon2.setImageResource(R.drawable.ic_cloudy);
                break;
            case 4:
            case 104:
                cuacaIcon2.setImageResource(R.drawable.ic_heavy_cloudy);
                break;
            case 60:
            case 61:
            case 63:
                cuacaIcon2.setImageResource(R.drawable.ic_rain);
                break;
            case 95:
            case 97:
                cuacaIcon2.setImageResource(R.drawable.ic_storm);
                break;
        }

    }

}

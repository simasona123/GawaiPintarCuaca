package com.example.gpc1.menus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpc1.background.LocationTask;
import com.example.gpc1.Preferences;
import com.example.gpc1.R;
import com.example.gpc1.background.XMLParsingTask;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, LocationTask.AsyncResponse, XMLParsingTask.XMLParsingTaskResponses {

    private static final String LOG_TAG = "MainActivity";
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    String provinsi, kabupaten;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm");
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd MMMM HH:mm");

    private TextView lokasiMainTextView, waktu, waktu1, waktu2, suhu, suhu1, suhu2
            , kelembaban
            , kelembaban1
            , kelembaban2;
    private ImageView cuacaIcon, cuacaIcon1, cuacaIcon2;

    private int cuacaIconId, cuacaIcon1Id, cuacaIcon2Id;

    LocationRequest locationRequest;
    FusedLocationProviderClient fusedLocationProviderClient;
    Geocoder geocoder;
    String lokasi;

    private SharedPreferences sharedPreferences;
    private Preferences preferences = new Preferences();
    private final String sharedPrefFile = preferences.SHARED_PRE_FILE;

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

        if (savedInstanceState != null){
            lokasiMainTextView.setText(savedInstanceState.getString("Lokasi Terakhir"));
            waktu.setText(savedInstanceState.getString("waktu"));
            waktu1.setText(savedInstanceState.getString("waktu1"));
            waktu2.setText(savedInstanceState.getString("waktu2"));
            suhu.setText(savedInstanceState.getString("suhu"));
            suhu1.setText(savedInstanceState.getString("suhu1"));
            suhu2.setText(savedInstanceState.getString("suhu2"));
            kelembaban.setText(savedInstanceState.getString("kelembaban"));
            kelembaban1.setText(savedInstanceState.getString("kelembaban1"));
            kelembaban2.setText(savedInstanceState.getString("kelembaban2"));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if (sharedPreferences.getString(preferences.KEY_UUID, null) == null){
            String uuID = UUID.randomUUID().toString();
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
            preferencesEditor.putString(preferences.KEY_UUID, uuID);
            preferencesEditor.putString(preferences.MODEL, Build.MODEL);
            preferencesEditor.putString(preferences.VERSION_RELEASE, Build.VERSION.RELEASE);
            preferencesEditor.apply();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }
    @Override
    public void processFinish(String kabupaten, String provinsi, String lokasi) {
        try{
            this.kabupaten = kabupaten;
            this.provinsi = provinsi;
            this.lokasi = lokasi;
            lokasiMainTextView.setText(lokasi);
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if(cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()){
                new XMLParsingTask(this, kabupaten, provinsi).execute();
            }
            else{
                Toast.makeText(this, "Butuh Koneksi Internet", Toast.LENGTH_SHORT).show();
            }
        }
        catch (NullPointerException e){
            Toast.makeText(this, "GPS dan izin lokasi dibutuhkan", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("waktu", waktu.getText().toString());
        outState.putString("waktu1", waktu1.getText().toString());
        outState.putString("waktu2", waktu2.getText().toString());
        outState.putString("suhu", suhu.getText().toString());
        outState.putString("suhu1", suhu1.getText().toString());
        outState.putString("suhu2", suhu2.getText().toString());
        outState.putString("kelembaban", kelembaban.getText().toString());
        outState.putString("kelembaban1", kelembaban1.getText().toString());
        outState.putString("kelembaban2", kelembaban2.getText().toString());
        outState.putInt("cuacaIcon", cuacaIconId);
        outState.putInt("cuacaIcon", cuacaIcon1Id);
        outState.putInt("cuacaIcon", cuacaIcon2Id);
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
                    Toast.makeText(this, "Harap Setujui izin permintaan lokasi dan nyalakan GPS", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void locationAccessPermission() {
        sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                + ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) != PackageManager.PERMISSION_GRANTED
        && sharedPreferences.getInt("Permisi", 0) != 1){
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
            preferencesEditor.putInt("Permisi", 1);
            preferencesEditor.apply();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 2 );
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Intent intent = new Intent (Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(),null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
            createLocationRequest();
            geocoder = new Geocoder(this, Locale.getDefault());
            new LocationTask(this,this, geocoder, fusedLocationProviderClient, lokasiMainTextView).execute();
        }
    }
    private void createLocationRequest (){
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(20 * 1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void processFinish(ArrayList<Integer> kelembabanParsing, ArrayList<Float> suhuParsing, ArrayList<Integer> kodeCuaca, ArrayList<Date> waktuCuaca, String s) {
        if(waktuCuaca.size() == 0 || waktuCuaca == null){
            waktu.setText("Tidak dapat menemukan prediksi cuaca");
            waktu1.setText("Tidak dapat" +"\n" + "menemukan" +"\n" + "prediksi cuaca");
            waktu2.setText("Tidak dapat" +"\n" + "menemukan" + "\n" + "prediksi cuaca");
            suhu.setText("-");
            suhu1.setText("-");
            suhu2.setText("-");
            kelembaban.setText("-");
            kelembaban1.setText("-");
            kelembaban2.setText("-");
        }
        else if(s.equals("Tidak ada Lokasi")){
            lokasiMainTextView.setText(lokasi);
        }
        else{
            String waktuText = simpleDateFormat.format(waktuCuaca.get(0));
            String waktu1Text = simpleDateFormat1.format(waktuCuaca.get(1));
            String waktu2Text = simpleDateFormat1.format(waktuCuaca.get(2));
            waktu.setText(waktuText);
            waktu1.setText(waktu1Text);
            waktu2.setText(waktu2Text);
            suhu.setText(suhuParsing.get(0).toString());
            suhu1.setText(suhuParsing.get(2).toString());
            suhu2.setText(suhuParsing.get(4).toString());
            kelembaban.setText(kelembabanParsing.get(0).toString());
            kelembaban1.setText(kelembabanParsing.get(1).toString());
            kelembaban2.setText(kelembabanParsing.get(2).toString());
            cuacaIconId =  kodeCuaca.get(0);
            cuacaIcon1Id = kodeCuaca.get(1);
            cuacaIcon2Id = kodeCuaca.get(2);
            gambarCuaca(cuacaIcon, cuacaIconId);
            gambarCuaca(cuacaIcon1, cuacaIcon1Id);
            gambarCuaca(cuacaIcon2, cuacaIcon2Id);
        }
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

}

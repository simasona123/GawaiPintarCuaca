package com.example.gpc1.background;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;

import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gpc1.DataModel;
import com.example.gpc1.DatabaseHelper;
import com.example.gpc1.NotificationGPC;
import com.example.gpc1.Preferences;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

public class IntentServicePerekamanData extends Service implements SensorEventListener {
    NotificationManager notificationManager;
    NotificationGPC notificationGPC;
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");

    private SensorManager sensorManager;
    private Sensor mSuhuCPU;
    private Intent intent;
    SharedPreferences sharedPreferences;

    String timeStamp; //Sudah
    private double longitude; //Sudah
    private double latitude; //Sudah
    private double altitude; //Sudah
    private double altitude1; //Sudah
    private float suhuBaterai; //sudah
    private float suhuUdara = 0; //sudah

    Location lastLocation;
    Location currentLocation;

    DataModel dataRekaman = new DataModel();
    DatabaseHelper databaseHelper = new DatabaseHelper(this);

    ListenerLocation listenerLocation = new ListenerLocation();

    public IntentServicePerekamanData() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationGPC = new NotificationGPC(this, notificationManager);
        startForeground(1, notificationGPC.notification(this, "Memulai Perekaman Data"));
        this.intent = intent;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mSuhuUdara = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSuhuCPU = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        Sensor mKelembabanUdara = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        Sensor mTekananUdara = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        if(mTekananUdara != null){
            sensorManager.registerListener(this, mTekananUdara, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(mSuhuUdara != null){
            sensorManager.registerListener(this, mSuhuUdara, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(mKelembabanUdara != null){
            sensorManager.registerListener(this, mKelembabanUdara, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if(mSuhuCPU != null){
            sensorManager.registerListener(this, mSuhuCPU, SensorManager.SENSOR_DELAY_NORMAL );
        }
        Calendar calendar = Calendar.getInstance();
//        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); #untuk mengubah menjadi utc
        timeStamp = simpleDateFormat.format(calendar.getTime());
        System.out.println("Calendar = " + timeStamp);
        dataRekaman.setTimeStamp(timeStamp);
        suhuBaterai = readBatteryTemp(this);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Izin Lokasi Bom");
            notificationGPC.deliverNotification("Akses Lokasi Diperlukan Pada Setting Aplikasi");
        }
        else{
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(listenerLocation);
        }
    }

    private boolean statusBaterai(Intent intent) {
        int baterai = intent.getIntExtra("statusBaterai", -1);
        //Sudah, Jika false Tidak di dcharge, jika true dicharge
        return baterai == BatteryManager.BATTERY_STATUS_CHARGING
                || baterai == BatteryManager.BATTERY_STATUS_FULL;
    }

    @SuppressLint("ObsoleteSdkInt")
    private boolean statusLayar() {
        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
        //sudah, true jika nyala, false jika mati
        boolean statusLayar;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH){
            statusLayar = pm.isInteractive();
        }
        else{
            statusLayar = pm.isScreenOn();
        }
        return statusLayar;
    }

    private float readBatteryTemp (Context context){
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        return ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)) / 10;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float currentValue = event.values[0];
        switch (sensorType){
            case Sensor.TYPE_AMBIENT_TEMPERATURE :
                suhuUdara = currentValue;
                dataRekaman.setSuhuUdara(suhuUdara);
                System.out.println("Sensor Suhu Udara = " + suhuUdara);
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                //sudah
                dataRekaman.setKelembabanUdara(currentValue);
                System.out.println("Sensor Kelembaban = " + suhuUdara);
                break;
            case Sensor.TYPE_PRESSURE:
                //sudah
                dataRekaman.setTekananUdara(currentValue);
                System.out.println("Sensor Tekanan udara = " + suhuUdara);
                break;
            case Sensor.TYPE_TEMPERATURE:
                //sudah
                dataRekaman.setCpuTemperatur(currentValue);
                System.out.println("Sensor SuhuCPU = " + suhuUdara);
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double getCurrentCPUTemperature() {
        String [] dirs = {"sys/class/thermal/thermal_zone",
        };
        ArrayList <Double> suhu = new ArrayList<>();
        for (String dir : dirs) {
            for(int i = 0 ; i <= 90 ;i ++){
                try {
                    Double val = OneLineReader.getValue(dir + i  +"/temp");
                    File file = new File (dir + i + "/type");
                    Scanner scanner = new Scanner(file);
                    String type = scanner.nextLine();
                    String pattern = "(?i)(.*)(cpu)(.*)";
                    System.out.println(dir + i +"/temp" + " " + val );
                    if (type.matches(pattern)){
                        System.out.println("Type = " + type);
                        suhu.add(val);
                    }
                } catch (Exception e) {
//                    e.printStackTrace();
                }
            }
        }
        double temp = 0.0;
        for (int i = 0; i < suhu.size() ; i++){
            double suhuAnggota = suhu.get(i);
            if (suhuAnggota > 10000){
                suhuAnggota = suhuAnggota/1000;
            }
            else if (suhuAnggota > 1000){
                suhuAnggota = suhuAnggota /100;
            }
            else if (suhuAnggota > 100){
                suhuAnggota = suhuAnggota/10;
            }
            temp = temp + suhuAnggota;
        }
        temp = temp / suhu.size();
        System.out.println("Suhu = " + suhu);
        return temp;
    }

    private class ListenerLocation implements OnSuccessListener<Location> {
        @Override
        public void onSuccess(Location location) {
            retrieveLocation(location);
        }
    }

    @SuppressLint("MissingPermission")
    private void retrieveLocation(Location location) {
        if(location != null){ //Jangan Lupa diganti tidak sama dengan untuk penggunaan
            System.out.println("Last Location a = " + location );
            lastLocation = location;
        }
        FusedLocationProviderClient fusedLocationProviderClient1 = LocationServices.getFusedLocationProviderClient(this);
        CancellationToken cancellationToken = new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                System.out.println("Apakah Cancel Request?");
                return false;
            }
            @NonNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                System.out.println("Cancel Request");
                return null;
            }
        };
        fusedLocationProviderClient1.getCurrentLocation(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, cancellationToken).addOnSuccessListener(
                location3 -> {
                    currentLocation = location3;
                    System.out.println("Boma");
                    System.out.println("Location 3 " + currentLocation);
                    if (currentLocation != null){
                        updateLocation(currentLocation);
                    }
                    else{
                        updateLocation(lastLocation);
                    }
                });
    }
    private void updateLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
        dataRekaman.setLongitude(longitude);
        dataRekaman.setLatitude(latitude);
        dataRekaman.setAltitude(altitude);
        dataRekaman.setSuhuBaterai(suhuBaterai);
        //sudah
        dataRekaman.setDikirim(false);
        dataRekaman.setStatusLayar(statusLayar());
        dataRekaman.setStatusBaterai(statusBaterai(intent));
        if(mSuhuCPU == null){
            dataRekaman.setCpuTemperatur((float)getCurrentCPUTemperature());
        }
        System.out.println("LocationManager = " + latitude + ", " + longitude + ", "+ altitude);
        sharedPreferences = getSharedPreferences(Preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        if (sharedPreferences.getFloat(Preferences.LAT_RECENTLY, 1f) == 1f ||
                sharedPreferences.getFloat(Preferences.LAT_RECENTLY, 1f) - (float)latitude >= 0.0002f ||
        sharedPreferences.getFloat(Preferences.ALT1_RECENTLY, 0f) == 0f) {
            altiOnline1();
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
            preferencesEditor.putFloat(Preferences.LAT_RECENTLY, (float)latitude);
            preferencesEditor.apply();
        }
        else{
            dataRekaman.setAltitude1(sharedPreferences.getFloat(Preferences.ALT1_RECENTLY, 0f));
            databaseHelper.addData(dataRekaman);
            notificationGPC.deliverNotification("Perekaman Data Berhasil. Terima Kasih :D ");
            sensorManager.unregisterListener(IntentServicePerekamanData.this);
            stopService(intent);
        }


    }
    private void altiOnline1 () {
        RequestQueue requestQueue = Volley.newRequestQueue(IntentServicePerekamanData.this);
        String url = "https://api.opentopodata.org/v1/srtm30m?locations=" + latitude + "," + longitude + "&interpolation=cubic";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null
                , response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        JSONObject jsonObject = results.getJSONObject(0);
                        altitude1 = jsonObject.getDouble("elevation");
                        System.out.println("altitudeAPI = "+ altitude1);
                        dataRekaman.setAltitude1(altitude1);
                        databaseHelper.addData(dataRekaman);
                        requestQueue.stop();
                        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
                        preferencesEditor.putFloat(Preferences.ALT1_RECENTLY, (float)altitude1);
                        preferencesEditor.apply();
                        notificationGPC.deliverNotification("Perekaman Data Berhasil. Terima Kasih :D ");
                        System.out.println(dataRekaman.toString());
                        stopService(intent);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            dataRekaman.setAltitude1(0);
            requestQueue.stop();
            System.out.println("Error API");
            databaseHelper.addData(dataRekaman);
            notificationGPC.deliverNotification("Perekaman Data Berhasil. Terima Kasih :D ");
            System.out.println(dataRekaman.toString());
            stopService(intent);
        });
        requestQueue.add(request); //TODO Jangan dihapus/diubah
    }


}

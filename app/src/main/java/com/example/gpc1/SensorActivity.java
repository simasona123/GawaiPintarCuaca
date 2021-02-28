package com.example.gpc1;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SensorActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener, CpuUsageTask.CpuUsageTaskFinish {
    private static final String LOG_TAG = SensorActivity.class.getSimpleName();
    private final int STORAGE_PERMISSION_CODE = 1;
    SQLiteDatabase gpcDatabase;
    long milis;
    long x;

    private SensorManager sensorManager;
    private Sensor mSuhuUdara;
    private Sensor mSuhuCPU;
    private Sensor mKelembabanUdara;
    private Sensor mTekananUdara;

    private final String NO_SENSOR = "- ";
    private TextView cpuUsage;
    private TextView suhuBaterai;
    private TextView tekananUdara;
    private TextView suhuUdara;
    private TextView kelembabanUdara;
    private TextView uuID;

    private String key_UUID;
    private SharedPreferences sharedPreferences;
    Preferences preferences = new Preferences();

    private final int PEREKAMAN_DATA = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_2);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        requestPermissionStorage();

        sharedPreferences = getSharedPreferences(preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        System.out.println(sharedPreferences.getString(preferences.MODEL, null) + sharedPreferences.getString(preferences.VERSION_RELEASE, null));
        key_UUID = sharedPreferences.getString("key_UUID", null);

        suhuBaterai = findViewById(R.id.rtSuhuBat);
        tekananUdara = findViewById(R.id.rtTekananUdara);
        suhuUdara = findViewById(R.id.rtSuhuUdara);
        kelembabanUdara = findViewById(R.id.rtKelembabanUdara);
        uuID = findViewById(R.id.userID);
        cpuUsage = findViewById(R.id.rtCpu);

        if(mSuhuUdara == null){
            suhuUdara.setText(NO_SENSOR);
        }
        if(mKelembabanUdara == null){
            kelembabanUdara.setText(NO_SENSOR);
        }
        if(mTekananUdara == null){
            tekananUdara.setText(NO_SENSOR);
        }
        if(mSuhuCPU == null){
            cpuUsage.setText(NO_SENSOR);
            new CpuUsageTask(this).execute();
        }

        suhuBaterai.setText(batteryReadTemperature(this));
        uuID.setText(key_UUID);

        Calendar calendar = Calendar.getInstance();
        milis = calendar.getTimeInMillis();
        x = milis % (60*1000*3);
        startAlarm(this, calendar);
        createDatabase();
    }

    private void createDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
    }

    private void startAlarm(Context context, Calendar calendar) {
        Intent notifyIntent = new Intent(this, MyReceiver.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, PEREKAMAN_DATA, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + (1000*60*3 - x), notifyPendingIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestPermissionStorage();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestPermissionStorage() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            System.out.println("Request External Storage");
        }
    }


    private String batteryReadTemperature(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        float temp = ((float) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0))/10;
        return  String.valueOf(new DecimalFormat("##.##").format(temp));
    }

    public void bukaLogActivity(View view) {
        Intent bukaLog = new Intent(this,LogActivity.class);
        startActivity(bukaLog);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.page_1:
                Intent intent0 = new Intent (this, MainActivity.class);
                startActivity(intent0);
                break;
            case R.id.page_2:
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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSuhuUdara = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSuhuCPU = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        mKelembabanUdara  = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mTekananUdara = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
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
        try{
            sensorManager.unregisterListener(this);
        }
        catch (NullPointerException e){
            System.out.println(LOG_TAG + e + "1");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"On Stop");
        try{
            sensorManager.unregisterListener(this);
        }
        catch (NullPointerException e ){
            System.out.println(LOG_TAG + e + "2");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,"On Destroy");
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        float currentValue = event.values[0];
        switch (sensorType){
            case Sensor.TYPE_AMBIENT_TEMPERATURE :
                suhuUdara.setText(new DecimalFormat("##.##").format(currentValue));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                kelembabanUdara.setText(new DecimalFormat("###.#").format(currentValue));
                break;
            case Sensor.TYPE_PRESSURE:
                tekananUdara.setText(new DecimalFormat("####.##").format(currentValue));
                break;
            case Sensor.TYPE_TEMPERATURE:
                cpuUsage.setText(new DecimalFormat("##.##").format(currentValue));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void processFinish(double cpuTemperature) {
        cpuUsage.setText(new DecimalFormat("##.##").format(cpuTemperature));
    }
}
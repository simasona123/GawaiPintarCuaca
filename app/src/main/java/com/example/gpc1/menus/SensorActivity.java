package com.example.gpc1.menus;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gpc1.Constants;
import com.example.gpc1.receiver.ReceiverPengirimanData;
import com.example.gpc1.background.CpuUsageTask;
import com.example.gpc1.datamodel.DatabaseHelper;
import com.example.gpc1.background.IntentServicePerekamanData;
import com.example.gpc1.receiver.ReceiverPerekamanData;
import com.example.gpc1.Preferences;
import com.example.gpc1.R;
import com.example.gpc1.receiver.ReceiverPerubahanJaringan;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.Calendar;

public class SensorActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener, SensorEventListener, CpuUsageTask.CpuUsageTaskFinish {
    private static final String LOG_TAG = SensorActivity.class.getSimpleName();
    long milis;
    long x;

    private SensorManager sensorManager;
    private Sensor mSuhuUdara;
    private Sensor mSuhuCPU;
    private Sensor mKelembabanUdara;
    private Sensor mTekananUdara;

    private TextView suhuCpu;
    private TextView tekananUdara;
    private TextView suhuUdara;
    private TextView kelembabanUdara;

    private static final int JOB_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_2);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        requestPermissionStorage();
        createDatabase();
        SharedPreferences sharedPreferences = getSharedPreferences(Preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        System.out.println(sharedPreferences.getString(Preferences.MODEL, null)+ "  " +
                sharedPreferences.getString(Preferences.VERSION_RELEASE, null));
        String key_UUID = sharedPreferences.getString("key_UUID", null);

        TextView suhuBaterai = findViewById(R.id.rtSuhuBat);
        tekananUdara = findViewById(R.id.rtTekananUdara);
        suhuUdara = findViewById(R.id.rtSuhuUdara);
        kelembabanUdara = findViewById(R.id.rtKelembabanUdara);
        TextView uuID = findViewById(R.id.userID);
        suhuCpu = findViewById(R.id.rtCpu);

        String NO_SENSOR = "- ";
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
            suhuCpu.setText(NO_SENSOR);
            new CpuUsageTask(this).execute();
        }
        suhuBaterai.setText(batteryReadTemperature(this));
        uuID.setText(key_UUID);
        Calendar calendar = Calendar.getInstance();
        startAlarmRecordData(this, calendar); //TODO Rekam data offline dan pengiriman data
        rekamDataSaatBukaMenu(); //TODO Rekam Data Saat Buka Menu
    }

    private void rekamDataSaatBukaMenu(){
        Intent intent1 = this.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int baterai = intent1.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        Intent intentService = new Intent(this, IntentServicePerekamanData.class);
        intentService.putExtra("statusBaterai", baterai);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(intentService);
        } else {
            this.startService(intentService);
        }
    }

    private void startAlarmRecordData(Context context, Calendar calendar) {
        Intent notifyIntent = new Intent(this, ReceiverPerekamanData.class);
        PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(context, 0, notifyIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        milis = calendar.getTimeInMillis();
        x = milis % (60 * 1000 * Constants.PERIODE_REKAMAN_MENIT);
        if (Build.VERSION.SDK_INT >= 19) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, milis +
                    (1000 * 60 * Constants.PERIODE_REKAMAN_MENIT - x), notifyPendingIntent);
        }
        else{
            alarmManager.set(AlarmManager.RTC_WAKEUP, milis +
                    (1000 * 60 * Constants.PERIODE_REKAMAN_MENIT - x), notifyPendingIntent);
        }
    }

    private void createDatabase() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int STORAGE_PERMISSION_CODE = 1;
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
        Intent bukaLog = new Intent(this, LogActivity.class);
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
                suhuCpu.setText(new DecimalFormat("##.##").format(currentValue));
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void processFinish(double cpuTemperature) {
        suhuCpu.setText(new DecimalFormat("##.##").format(cpuTemperature));
    }

}
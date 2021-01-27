package com.example.gpc1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(LOG_TAG,"Memulai Program");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
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
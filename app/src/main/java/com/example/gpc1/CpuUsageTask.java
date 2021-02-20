package com.example.gpc1;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.StrictMode;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.requestPermissions;

public class CpuUsageTask extends AsyncTask <Void, Void, Double> {

    double cpuTemperature;
    CpuUsageTaskFinish listener;
    private final WeakReference<Context> context;
    private OneLineReader oneLineReader;

    public interface CpuUsageTaskFinish{
        void processFinish(double cpuTemperature);
    }

    @SuppressWarnings("deprecation")
    public CpuUsageTask(Context context) {
        this.context = new WeakReference<>(context);
        listener = (CpuUsageTaskFinish) context;
    }

    @Override
    protected Double doInBackground(Void... voids) {
        cpuTemperature = getCurrentCPUTemperature();
        System.out.println(CpuUsageTask.class.getSimpleName() + "  Cpu Temperature " +  " = " + cpuTemperature + " C");
        return cpuTemperature;

    }

    private double getCurrentCPUTemperature() {
        String [] dirs = {"sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
                "sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp",
                "sys/class/thermal/thermal_zone1/temp",
                "sys/class/thermal/thermal_zone0/temp",
                "sys/class/thermal/thermal_zone2/temp",
                "sys/class/thermal/thermal_zone3/temp",
                "/sys/devices/virtual/thermal/thermal_zone0/temp",
                "sys/class/i2c-adapter/i2c-4/4-004c/temperature",
                "sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature",
                "sys/devices/platform/omap/omap_temp_sensor.0/temperature",
                "sys/devices/platform/tegra_tmon/temp1_input",
                "sys/kernel/debug/tegra_thermal/temp_tj",
                "sys/devices/platform/s5p-tmu/temperature",
                "sys/devices/virtual/thermal/thermal_zone0/temp",
                "sys/devices/virtual/thermal/thermal_zone1/temp",
                "sys/devices/virtual/thermal/thermal_zone2/temp",
                "sys/devices/virtual/thermal/thermal_zone3/temp",
                "sys/class/hwmon/hwmon0/device/temp1_input",
                "sys/class/hwmon/hwmonX/temp1_input",
                "sys/devices/platform/s5p-tmu/curr_temp",
                "/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp",
        };
        ArrayList <Double> suhu = new ArrayList<>();
        Process process;
        String line;
        BufferedReader reader;
        RandomAccessFile reader1;
        for (String dir : dirs) {
            try {
                Double val = OneLineReader.getValue(dir);
                suhu.add(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double temp = 0.0;
        for (int i = 0; i < suhu.size() ; i++){
            double suhuAnggota = suhu.get(i);
            if (suhuAnggota > 10000){
                suhuAnggota = suhuAnggota/1000;
            }
            if (suhuAnggota > 100){
                suhuAnggota = suhuAnggota/100;
            }
                temp = temp + suhuAnggota;
        }
        temp = temp / suhu.size();
        System.out.println("Suhu = " + suhu);
        return temp;
    }

    @Override
    protected void onPostExecute(Double cpuTemperature) {
        super.onPostExecute(cpuTemperature);
        listener.processFinish(cpuTemperature);
    }
}

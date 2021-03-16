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
import java.util.Scanner;

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

    @Override
    protected void onPostExecute(Double cpuTemperature) {
        super.onPostExecute(cpuTemperature);
        listener.processFinish(cpuTemperature);
    }
}

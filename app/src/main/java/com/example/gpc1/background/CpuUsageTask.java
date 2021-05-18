package com.example.gpc1.background;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String [] dirs = {"sys/class/thermal/thermal_zone",};
        ArrayList <Double> suhu = new ArrayList<>();
        for (String dir : dirs) {
            for(int i = 0 ; i <= 90 ;i ++){
                try {
                    Double val = OneLineReader.getValue(dir + i  +"/temp"); // "sys/class/thermal/thermal_zone0/temp"
                    File file = new File (dir + i + "/type");  // "sys/class/thermal/thermal_zone0/type"
                    Scanner scanner = new Scanner(file);
                    String type = scanner.nextLine();
                    String pattern = "(i?)(.*)(cpu)(.*)";
                    Pattern pattern1 = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern1.matcher(type);
                    System.out.println("Type = " + type);
                    System.out.println(dir + i +"/temp" + " " + val );
                    if (matcher.matches()){
                        System.out.println("Type = " + type);
                        suhu.add(val);
                    }
                } catch (Exception e) {
                       e.printStackTrace();
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

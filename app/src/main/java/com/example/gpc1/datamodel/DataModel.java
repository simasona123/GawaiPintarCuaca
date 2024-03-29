package com.example.gpc1.datamodel;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;


public class DataModel {
    private int id;
    private String timeStamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private double altitude1; //altitude1 API
    private float suhuUdara;
    private float kelembabanUdara;
    private float suhuBaterai;
    private float tekananUdara;
    private float cpuTemperatur;
    private boolean dikirim;
    private boolean statusLayar;
    private boolean statusBaterai;
    
    public DataModel(int id, String timeStamp, double latitude, double longitude,
                     double altitude, double altitude1, float suhuUdara, float kelembabanUdara,
                     float suhuBaterai, float tekananUdara, float cpuTemperatur,
                     boolean statusLayar, boolean statusBaterai) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.altitude1 = altitude1;
        this.suhuUdara = suhuUdara;
        this.kelembabanUdara = kelembabanUdara;
        this.suhuBaterai = suhuBaterai;
        this.tekananUdara = tekananUdara;
        this.cpuTemperatur = cpuTemperatur;
        this.statusLayar = statusLayar;
        this.statusBaterai = statusBaterai;
    }

    public DataModel(int id, String timeStamp, double latitude, double longitude, double altitude,
                     double altitude1, float suhuUdara, float kelembabanUdara, float suhuBaterai,
                     float tekananUdara, float cpuTemperatur, boolean dikirim, boolean statusLayar,
                     boolean statusBaterai) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.altitude1 = altitude1;
        this.suhuUdara = suhuUdara;
        this.kelembabanUdara = kelembabanUdara;
        this.suhuBaterai = suhuBaterai;
        this.tekananUdara = tekananUdara;
        this.cpuTemperatur = cpuTemperatur;
        this.dikirim = dikirim;
        this.statusLayar = statusLayar;
        this.statusBaterai = statusBaterai;
    }

    public DataModel() {
    }

    @Override
    public String toString() {


        return  "id: "+ this.id +
                "{ time: "+ timeStamp +
                "; lat: " + new DecimalFormat("#.####").format(latitude)+
                "; long: " +new DecimalFormat("#.####").format(longitude)+
                "; alt: " + new DecimalFormat("#.##").format(altitude) +
                "; alt1: " + new DecimalFormat("#.##").format(altitude1) +
                "; suhu: " + suhuUdara +
                "; rh: " + kelembabanUdara +
                "; suhuBat: " + suhuBaterai +
                "; tekanan: " + tekananUdara +
                "; suhuCPU: " + cpuTemperatur +
                "; kirim: " + dikirim +
                "; layar: " + statusLayar +
                "; charging: " + statusBaterai +
                '}';
    }


    public double getAltitude1() {
        return altitude1;
    }

    public void setAltitude1(double altitude1) {
        this.altitude1 = altitude1;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSuhuUdara() {
        return suhuUdara;
    }

    public void setSuhuUdara(float suhuUdara) {
        this.suhuUdara = suhuUdara;
    }

    public float getKelembabanUdara() {
        return kelembabanUdara;
    }

    public void setKelembabanUdara(float kelembabanUdara) {
        this.kelembabanUdara = kelembabanUdara;
    }

    public float getSuhuBaterai() {
        return suhuBaterai;
    }

    public void setSuhuBaterai(float suhuBaterai) {
        this.suhuBaterai = suhuBaterai;
    }

    public float getTekananUdara() {
        return tekananUdara;
    }

    public void setTekananUdara(float tekananUdara) {
        this.tekananUdara = tekananUdara;
    }

    public float getCpuTemperatur() {
        return cpuTemperatur;
    }

    public void setCpuTemperatur(float cpuTemperatur) {
        this.cpuTemperatur = cpuTemperatur;
    }

    public boolean isDikirim() {
        return dikirim;
    }

    public void setDikirim(boolean dikirim) {
        this.dikirim = dikirim;
    }

    public boolean isStatusLayar() {
        return statusLayar;
    }

    public void setStatusLayar(boolean statusLayar) {
        this.statusLayar = statusLayar;
    }

    public boolean isStatusBaterai() {
        return statusBaterai;
    }

    public void setStatusBaterai(boolean statusBaterai) {
        this.statusBaterai = statusBaterai;
    }

    public int getId() {
        return id;
    }

    public JSONObject toJSON () {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("timestamp", this.timeStamp);
            jsonObject.put("latitude", this.latitude);
            jsonObject.put("longitude", this.longitude);
            jsonObject.put("altitude", this.altitude);
            jsonObject.put("altitude1", this.altitude1);
            jsonObject.put("suhu_udara", this.suhuUdara);
            jsonObject.put("kelembaban_udara", this.kelembabanUdara);
            jsonObject.put("suhu_baterai", this.suhuBaterai);
            jsonObject.put("tekanan_udara", this.tekananUdara);
            jsonObject.put("suhu_cpu", this.cpuTemperatur);
            jsonObject.put("status_layar", this.statusLayar);
            jsonObject.put("status_charging", this.statusBaterai);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

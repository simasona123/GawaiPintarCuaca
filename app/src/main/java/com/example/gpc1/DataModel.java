package com.example.gpc1;

public class DataModel {
    private String timeStamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private float suhuUdara;
    private float kelembabanUdara;
    private float suhuBaterai;
    private float tekananUdara;
    private float cpuTemperatur;
    private boolean dikirim;
    private boolean statusLayar;
    private boolean statusBaterai;

    public DataModel(String timeStamp, double latitude, double longitude,
                     double altitude, float suhuUdara, float kelembabanUdara,
                     float suhuBaterai, float tekananUdara, float cpuTemperatur,
                     boolean dikirim, boolean statusLayar, boolean statusBaterai) {
        this.timeStamp = timeStamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
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
        return "DataModel{" +
                ", timeStamp=" + timeStamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", suhuUdara=" + suhuUdara +
                ", kelembabanUdara=" + kelembabanUdara +
                ", suhuBaterai=" + suhuBaterai +
                ", tekananUdara=" + tekananUdara +
                ", cpuTemperatur=" + cpuTemperatur +
                ", dikirim=" + dikirim +
                ", statusLayar=" + statusLayar +
                ", statusBaterai=" + statusBaterai +
                '}';
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
}

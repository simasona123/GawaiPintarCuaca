package com.example.gpc1;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static final Map<String, String> alamatXml = initMap();


    private static Map<String, String> initMap(){
        Map<String, String> a = new HashMap<>();
        a.put("Jawa Timur", "https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-JawaTimur.xml");
        a.put("DKI Jakarta", "https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-DKIJakarta.xml");
        a.put("Lampung", "https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-Lampung.xml");
        a.put("Banten", "https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-Banten.xml");
        a.put("Sumatera Utara", "https://data.bmkg.go.id/datamkg/MEWS/DigitalForecast/DigitalForecast-SumateraUtara.xml");
        a.put("Jawa Tengah", "https://data.bmkg.go.id/DataMKG/MEWS/DigitalForecast/DigitalForecast-JawaTengah.xml");
        a.put("Special Region of Yogyakarta", "https://data.bmkg.go.id/DataMKG/MEWS/DigitalForecast/DigitalForecast-DIYogyakarta.xml");
        return Collections.unmodifiableMap(a);
    }
    public static final String URL_SERVER = "http://192.168.1.72:80/api/send_data";
    public static final int PERIODE_REKAMAN_MENIT = 2; //TODO Atur waktu 10 menit sesuai dengan permintaan teman
    public static final int PERIODE_PENGIRIMAN_DATA = 180; //TODO Atur waktu menit sekali
}
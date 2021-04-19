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
        return Collections.unmodifiableMap(a);
    }
    public static final int PERIODE_REKAMAN_MENIT = 10; //TODO Atur waktu 10 menit sesuai dengan permintaan teman
    public static final int PERIODE_PENGIRIMAN_DATA = 120; //TODO Atur waktu menit sekali
}
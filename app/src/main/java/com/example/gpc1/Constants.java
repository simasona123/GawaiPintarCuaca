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
        return Collections.unmodifiableMap(a);
    }
}

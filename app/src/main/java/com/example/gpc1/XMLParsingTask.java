package com.example.gpc1;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class XMLParsingTask extends AsyncTask <Void, Void, String> {

    private final String TAG = XMLParsingTask.class.getSimpleName();
    private XmlPullParser xmlPullParser;
    private InputStream inputStream;
    private URL url;
    private String tagName;
    private int tagEventType;

    private final String kabupaten;
    private final String provinsi;
    String alamatLinkProvinsi;
    Date waktu;
    private final String datePattern = "yyyyMMddHHmm";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

    private ArrayList<Integer> kelembaban = new ArrayList<>();
    private ArrayList<Float> suhu = new ArrayList<>();
    private ArrayList<Integer> kodeCuaca = new ArrayList<>();

    int x;

    public XMLParsingTask(String kabupaten1, String provinsi1) {
        this.kabupaten = kabupaten1;
        this.provinsi = provinsi1;
    }

    @Override
    protected String doInBackground(Void... voids){
        alamatLinkProvinsi = Constants.alamatXml.get(provinsi);
        System.out.println(alamatLinkProvinsi);
        waktu = Calendar.getInstance().getTime();
        try {
            url = new URL(alamatLinkProvinsi);

        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try{
            xmlPullParser = Xml.newPullParser();
            xmlPullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES,true);
            System.out.println("URL = " + url);
            xmlPullParser.setInput(getInputStream(url), "UTF-8");
            xmlPullParser.nextTag();
            xmlPullParser.nextTag();
            tagName = xmlPullParser.getName();
            tagEventType = xmlPullParser.getEventType();
            System.out.println(TAG + " "+ tagName + " "+ tagEventType);
            while (xmlPullParser.next()!= xmlPullParser.END_TAG){
                if (xmlPullParser.getEventType() != xmlPullParser.START_TAG){
                    continue;
                }
                tagName = xmlPullParser.getName();
                tagEventType = xmlPullParser.getEventType();
                System.out.println("A = " + " "+ tagName + " "+ tagEventType);
                if (tagName.equals("area")){
                    String description = xmlPullParser.getAttributeValue(null, "description");
                    System.out.println("Description = " + description);
                    System.out.println(description.equals(kabupaten));
                    if(description.equals(kabupaten)){
                        while(xmlPullParser.next() != XmlPullParser.END_TAG){
                            if (xmlPullParser.getEventType() != xmlPullParser.START_TAG){
                                continue;
                            }
                            if (xmlPullParser.getName().equals("parameter")){
                                String id = xmlPullParser.getAttributeValue(null, "id");
                                System.out.println("ID = " + id);
                                x = 0;
                                if (id.equals("hu")){
                                    while(xmlPullParser.next() != xmlPullParser.END_TAG){
                                        if(xmlPullParser.getEventType() != xmlPullParser.START_TAG){
                                            continue;
                                        }
                                        if (xmlPullParser.getName().equals("timerange")){
                                            String tanggal = xmlPullParser.getAttributeValue(null, "datetime");
                                            Date waktu1 = simpleDateFormat.parse(tanggal);
                                            assert waktu1 != null;
                                            if (waktu1.after(waktu) && x < 3){
                                                x = x + 1;
                                                System.out.println("x = " + x);
                                                while (xmlPullParser.next()!=xmlPullParser.END_TAG){
                                                    if(xmlPullParser.getEventType()!=xmlPullParser.START_TAG){
                                                        tagName = xmlPullParser.getName();
                                                        tagEventType = xmlPullParser.getEventType();
                                                        System.out.println("V = " + " "+ tagName + " "+ tagEventType);
                                                        continue;
                                                    }
                                                    if (xmlPullParser.getName().equals("value")) {
                                                        if(xmlPullParser.next() == xmlPullParser.TEXT){
                                                            int kelembaban1 = Integer.parseInt(xmlPullParser.getText());
                                                            kelembaban.add(kelembaban1);
                                                            System.out.println("Kelembaban 1 = " + kelembaban1);
                                                            xmlPullParser.next();xmlPullParser.next();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.END_TAG){
                                            xmlPullParser.next();
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.START_TAG){
                                            skip(xmlPullParser);
                                        }
                                    }
                                }
                                else if(id.equals("t")){
                                    while(xmlPullParser.next() != xmlPullParser.END_TAG){
                                        if(xmlPullParser.getEventType() != xmlPullParser.START_TAG){
                                            continue;
                                        }
                                        if (xmlPullParser.getName().equals("timerange")){
                                            String tanggal = xmlPullParser.getAttributeValue(null, "datetime");
                                            Date waktu1 = simpleDateFormat.parse(tanggal);
                                            assert waktu1 != null;
                                            if (waktu1.after(waktu) && x < 3){
                                                x = x + 1;
                                                System.out.println("x = " + x);
                                                while (xmlPullParser.next()!=xmlPullParser.END_TAG){
                                                    if(xmlPullParser.getEventType()!=xmlPullParser.START_TAG){
                                                        tagName = xmlPullParser.getName();
                                                        tagEventType = xmlPullParser.getEventType();
                                                        System.out.println("V = " + " "+ tagName + " "+ tagEventType);
                                                        continue;
                                                    }
                                                    if (xmlPullParser.getName().equals("value")) {
                                                        if(xmlPullParser.next() == xmlPullParser.TEXT){
                                                            Float suhu1 = Float.parseFloat(xmlPullParser.getText());
                                                            suhu.add(suhu1);
                                                            System.out.println("suhu 1 = " + suhu1);
                                                            xmlPullParser.next();xmlPullParser.next();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.END_TAG){
                                            xmlPullParser.next();
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.START_TAG){
                                            skip(xmlPullParser);
                                        }
                                    }
                                }
                                else if(id.equals("weather")){
                                    while(xmlPullParser.next() != xmlPullParser.END_TAG){
                                        if(xmlPullParser.getEventType() != xmlPullParser.START_TAG){
                                            continue;
                                        }
                                        if (xmlPullParser.getName().equals("timerange")){
                                            String tanggal = xmlPullParser.getAttributeValue(null, "datetime");
                                            Date waktu1 = simpleDateFormat.parse(tanggal);
                                            assert waktu1 != null;
                                            if (waktu1.after(waktu) && x < 3){
                                                x = x + 1;
                                                System.out.println("x = " + x);
                                                while (xmlPullParser.next()!=xmlPullParser.END_TAG){
                                                    if(xmlPullParser.getEventType()!=xmlPullParser.START_TAG){
                                                        tagName = xmlPullParser.getName();
                                                        tagEventType = xmlPullParser.getEventType();
                                                        System.out.println("V = " + " "+ tagName + " "+ tagEventType);
                                                        continue;
                                                    }
                                                    if (xmlPullParser.getName().equals("value")) {
                                                        if(xmlPullParser.next() == xmlPullParser.TEXT){
                                                            int kelembaban1 = Integer.parseInt(xmlPullParser.getText());
                                                            kodeCuaca.add(kelembaban1);
                                                            System.out.println("kodeCuaca = " + kelembaban1);
                                                            xmlPullParser.next();xmlPullParser.next();
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.END_TAG){
                                            xmlPullParser.next();
                                        }
                                        if (xmlPullParser.getEventType()==xmlPullParser.START_TAG){
                                            skip(xmlPullParser);
                                        }
                                    }
                                }
                            }
                            if (xmlPullParser.getEventType()==xmlPullParser.END_TAG){
                                xmlPullParser.next();
                            }
                            if (xmlPullParser.getEventType()==xmlPullParser.START_TAG){
                                skip(xmlPullParser);
                            }
                        }
                    }
                }
                if (xmlPullParser.getEventType()==xmlPullParser.END_TAG){
                    xmlPullParser.next();
                }
                if (xmlPullParser.getEventType()==xmlPullParser.START_TAG){
                    skip(xmlPullParser);
                }
            }
        }
        catch (XmlPullParserException | IOException | ParseException e) {
            e.printStackTrace();
        }
        return "Berhasil";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        System.out.println(kelembaban);
        System.out.println(suhu);
        System.out.println(kodeCuaca);

    }

    private void skip (XmlPullParser xmlPullParser) throws XmlPullParserException, IOException{
        if (xmlPullParser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0 ){
            switch (xmlPullParser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private InputStream getInputStream(URL url) throws IOException{
        try {
            return url.openConnection().getInputStream();
        } catch (IOException ioException) {
            ioException.printStackTrace();
            return null;
        }
    }

}

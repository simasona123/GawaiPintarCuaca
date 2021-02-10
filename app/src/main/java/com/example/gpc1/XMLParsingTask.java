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
import java.util.Calendar;
import java.util.Date;

public class XMLParsingTask extends AsyncTask <Void, Void, String> {

    private final String TAG = XMLParsingTask.class.getSimpleName();
    private XmlPullParser xmlPullParser;
    private InputStream inputStream;
    private URL url;
    private String tagName;
    private int tagEventType;

    private String kabupaten;
    private String provinsi;
    String alamatLinkProvinsi;
    Date waktu;
    private final String datePattern = "yyyyMMddHHmm";
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);

    public XMLParsingTask(String kabupaten1, String provinsi1) {
        this.kabupaten = kabupaten1;
        this.provinsi = provinsi1;
    }

    @Override
    protected String doInBackground(Void... voids){
        alamatLinkProvinsi = Constants.alamatXml.get(provinsi);
        System.out.println(alamatLinkProvinsi);
        waktu = Calendar.getInstance().getTime();
        System.out.println("Sekarang = " + waktu);

        try {
            url = new URL(alamatLinkProvinsi);

        } catch (MalformedURLException e) {
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
                    tagName = xmlPullParser.getName();
                    tagEventType = xmlPullParser.getEventType();
                    System.out.println("A = " + " "+ tagName + " "+ tagEventType);
                    continue;
                }
                tagName = xmlPullParser.getName();
                tagEventType = xmlPullParser.getEventType();
                System.out.println("B = " + " "+ tagName + " "+ tagEventType);
                if (tagName.equals("area")){
                    String description = xmlPullParser.getAttributeValue(null, "description");
                    System.out.println("Description = " + description);
                    System.out.println("Kabupaten = " + kabupaten);
                    System.out.println(description.equals(kabupaten));
                    System.out.println(description);
                    System.out.println(kabupaten);
                }
                skip(xmlPullParser);
            }
        }
        catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}

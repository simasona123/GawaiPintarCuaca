package com.example.gpc1;

import android.os.AsyncTask;

public class XMLParsingTask extends AsyncTask <Void, Void, String> {

    String kabupaten;
    String provinsi;
    String alamatLinkProvinsi;

    public XMLParsingTask(String kabupaten, String provinsi) {
        this.kabupaten = kabupaten;
        this.provinsi = provinsi;
    }

    @Override
    protected String doInBackground(Void... voids) {
        alamatLinkProvinsi = Constants.alamatXml.get(provinsi);
        System.out.println(alamatLinkProvinsi);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

}

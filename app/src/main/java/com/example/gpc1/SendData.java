    package com.example.gpc1;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gpc1.DataModel;
import com.example.gpc1.DatabaseHelper;
import com.example.gpc1.NotificationGPC;
import com.example.gpc1.Preferences;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.ArrayList;

public class SendData extends JobService {
    NotificationManager notificationManager;
    NotificationGPC notificationGPC;
    DatabaseHelper databaseHelper;
    ArrayList<DataModel> unsendingData;
    SharedPreferences sharedPreferences;
    boolean jobCancelled;

    @Override
    public boolean onStartJob(JobParameters params) {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationGPC = new NotificationGPC(this, notificationManager);
        notificationGPC.deliverNotification("Pengiriman Data");
        databaseHelper = new DatabaseHelper(this);
        sharedPreferences = getSharedPreferences(Preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                unsendingData = new ArrayList<>();
                unsendingData = databaseHelper.getUnsendingData();
                JSONArray jsonArray = new JSONArray();
                System.out.println("unsendingDataSize = " + unsendingData.size());
                for(int i = 0; i < unsendingData.size(); i++){
                    try {
                        jsonArray.put(i, unsendingData.get(i).toJSON());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("uuid", sharedPreferences.getString(Preferences.KEY_UUID, null));
                    jsonObject.put("model", sharedPreferences.getString(Preferences.MODEL, null));
                    jsonObject.put("api_version", sharedPreferences.getString(Preferences.VERSION_RELEASE, null));
                    jsonObject.put("data", (JSONArray) jsonArray );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendData(jsonObject, params);

            }
        }).start();
    }

    private void sendData(JSONObject jsonObject, JobParameters params) {

        String url = "http://192.168.1.72:80/api/send_data";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Berhasil");
                Log.i("boma", response.toString());
                notificationGPC.deliverNotification("Pengiriman Data Berhasil");
                int i = 0;
                for(DataModel data:unsendingData){
                    boolean status = databaseHelper.updateStatusKirim(data);
                    if (!status){
                        System.out.println("Gagal Update Status Kirim Data" + i);
                        break;
                    }
                    else{
                        System.out.println("Berhasil Update Status Kirim Data" + i);
                    }
                    i++;
                }
                jobFinished(params, true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Gagal");
                Log.i("Pengiriman Data", "Gagal " + error);
                notificationGPC.deliverNotification("Pengiriman Data Gagal");
                jobFinished(params, true);
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    //TODO Volley API, JSON Sudah bisa
    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }
}

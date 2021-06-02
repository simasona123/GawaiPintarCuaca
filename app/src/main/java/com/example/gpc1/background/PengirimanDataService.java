package com.example.gpc1.background;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.gpc1.Constants;
import com.example.gpc1.NotificationGPC;
import com.example.gpc1.Preferences;
import com.example.gpc1.datamodel.DataModel;
import com.example.gpc1.datamodel.DataModel1;
import com.example.gpc1.datamodel.DatabaseHelper;
import com.example.gpc1.receiver.ReceiverPerubahanJaringan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PengirimanDataService extends Service {
    private static final String TAG = "PengirimanDataService" ;
    private NotificationGPC notificationGPC;
    private Intent intent;
    private SharedPreferences sharedPreferences;
    private ArrayList<DataModel> unsendingData; //DataModel untuk data perekeman
    private final DatabaseHelper databaseHelper = new DatabaseHelper(this);
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
    int schedulingCount;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.intent = intent;
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationGPC = new NotificationGPC(this, notificationManager);
        startForeground(1, notificationGPC.notification(this, "Memulai Pengiriman Data"));
        sharedPreferences = getSharedPreferences(Preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor1 = sharedPreferences.edit();
        if(sharedPreferences.getInt("Jaringan", 0) == 1){
            schedulingCount = sharedPreferences.getInt(Preferences.SCHEDULING_COUNT, 0);
            schedulingCount += 1;
            preferencesEditor1.putInt(Preferences.SCHEDULING_COUNT, schedulingCount);
            preferencesEditor1.putInt("Jaringan", 0);
            preferencesEditor1.apply();
        }
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
        unsendingData = new ArrayList<>();
        unsendingData = databaseHelper.getUnsendingData(this);
        JSONArray jsonArray = new JSONArray();
        Log.i("pengiriman","unsendingDataSize = " + unsendingData.size());
        for (int i = 0; i < unsendingData.size(); i++) {
            try {
                jsonArray.put(i, unsendingData.get(i).toJSON());
            } catch (JSONException e) {
                Log.e(TAG, "JSONException");
            }
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uuid", sharedPreferences.getString(Preferences.KEY_UUID, null));
            jsonObject.put("model", sharedPreferences.getString(Preferences.MODEL, null));
            jsonObject.put("api_version", sharedPreferences.getString(Preferences.VERSION_RELEASE, null));
            jsonObject.put("data", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendData(jsonObject);
    }



    public PengirimanDataService() { }

    private void sendData(JSONObject jsonObject){
        String url = Constants.URL_SERVER;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, response -> {
            System.out.println("Berhasil");
            notificationGPC.deliverNotification("Pengiriman Data Berhasil");
            int i = 0;
            for(DataModel data:unsendingData){
                boolean status = databaseHelper.updateStatusKirim(data);
                if (!status){
                    Log.i("pengirim", "Gagal Update Status Kirim Data" + String.valueOf(i));
                }
                else{
                    Log.i("pengirim", "Berhasil Update Status Kirim Data" + String.valueOf(i));
                }
                i++;
            }
            Calendar calendar = Calendar.getInstance();
            String timestamp = simpleDateFormat.format(calendar.getTime());
            DataModel1 dataModel1 = new DataModel1(timestamp, "Terkirim");
            databaseHelper.addData1(dataModel1);
            int userMaks = 0;
            int userID = 0;
            try {
                userMaks = response.getInt("user_maks");
                userID = response.getInt("user");
                System.out.println(userMaks+" dan " + userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();
            preferencesEditor.putInt(Preferences.USER_MAKS, userMaks);
            preferencesEditor.putInt(Preferences.ID_USER, userID);
            preferencesEditor.putInt(Preferences.SCHEDULING_COUNT, 0);
            preferencesEditor.apply();
            System.out.println("Pengiriman Data Service");
            System.out.println("User Maks = " + sharedPreferences.getInt(Preferences.USER_MAKS,0));
            System.out.println("User ID = " + sharedPreferences.getInt(Preferences.ID_USER,0));
            stopService(intent);
        }, error -> {
            Intent intent1 = new Intent("com.example.ACTION");
            PackageManager packageManager = getPackageManager();
            List<ResolveInfo> infos = packageManager.queryBroadcastReceivers(intent1, 0);
            for (ResolveInfo info:infos){
                ComponentName componentName = new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
                intent1.setComponent(componentName);
                sendBroadcast(intent1);
            }
            Log.i("Pengiriman Data", "Gagal " + error);
            notificationGPC.deliverNotification("Pengiriman Data Gagal");
            Calendar calendar = Calendar.getInstance();
            String timestamp = simpleDateFormat.format(calendar.getTime());
            DataModel1 dataModel1 = new DataModel1(timestamp, "Tidak Terkirim");
            databaseHelper.addData1(dataModel1);
            stopService(intent);
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
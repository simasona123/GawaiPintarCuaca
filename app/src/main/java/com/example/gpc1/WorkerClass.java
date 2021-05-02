package com.example.gpc1;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WorkerClass extends Worker {
    NotificationManager notificationManager;
    NotificationGPC notificationGPC;
    DatabaseHelper databaseHelper;
    ArrayList<DataModel> unsendingData;
    SharedPreferences sharedPreferences;
    Context context;

    public WorkerClass(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationGPC = new NotificationGPC(context, notificationManager);
        notificationGPC.deliverNotification("Pengiriman Data");
        databaseHelper = new DatabaseHelper(context);
        sharedPreferences = context.getSharedPreferences(Preferences.SHARED_PRE_FILE, Context.MODE_PRIVATE);
        prepareSendData();
        Log.i("Coba Worker", "Berhasil");
        return Result.success();
    }

    private void prepareSendData() {
        unsendingData = new ArrayList<>();
        unsendingData = databaseHelper.getUnsendingData();
        JSONArray jsonArray = new JSONArray();
        System.out.println("unsendingDataSize = " + unsendingData.size());
        for (int i = 0; i < unsendingData.size(); i++) {
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
            jsonObject.put("data", (JSONArray) jsonArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendData(jsonObject);
    }

    private void sendData(JSONObject jsonObject) {
        String url = Constants.URL_SERVER;
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST, url, jsonObject, response -> {
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
                }, error -> {
            System.out.println("Gagal");
            Log.i("Pengiriman Data", "Gagal " + error);
            notificationGPC.deliverNotification("Pengiriman Data Gagal");
        });
        requestQueue.add(jsonObjectRequest);
    }
}

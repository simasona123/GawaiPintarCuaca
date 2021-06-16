    package com.example.gpc1;

    import android.app.NotificationManager;
    import android.app.job.JobParameters;
    import android.app.job.JobService;
    import android.content.Context;
    import android.content.SharedPreferences;
    import android.os.Build;
    import android.util.Log;

    import androidx.annotation.RequiresApi;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.android.volley.toolbox.Volley;
    import com.example.gpc1.datamodel.DataModel;
    import com.example.gpc1.datamodel.DataModel1;
    import com.example.gpc1.datamodel.DatabaseHelper;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.text.SimpleDateFormat;
    import java.util.ArrayList;
    import java.util.Calendar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SendData extends JobService {
    private static final String TAG = "SendData Job Service";
    NotificationManager notificationManager;
    NotificationGPC notificationGPC;
    DatabaseHelper databaseHelper;
    ArrayList<DataModel> unsendingData;
    SharedPreferences sharedPreferences;
    boolean jobCancelled;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");


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
        new Thread((Runnable) () -> {
            sharedPreferences = this.getSharedPreferences(Preferences.SHARED_PRE_FILE, Context.MODE_PRIVATE);
            unsendingData = new ArrayList<>();
            unsendingData = databaseHelper.getUnsendingData(this);
            JSONArray jsonArray = new JSONArray();
            System.out.println("unsendingDataSize = " + unsendingData.size());
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
            sendData(jsonObject, params);
        }).start();
    }

    private void sendData(JSONObject jsonObject, JobParameters params) {
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
                    System.out.println("Gagal Update Status Kirim Data" + i);
                    break;
                }
                else{
                    System.out.println("Berhasil Update Status Kirim Data" + i);
                }
                i++;
            }
            Calendar calendar = Calendar.getInstance();
            String timestamp = simpleDateFormat.format(calendar.getTime());
            DataModel1 dataModel1 = new DataModel1(timestamp, "JobScheduler, Terkirim");
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
//            preferencesEditor.putInt(Preferences.ID_USER, userID);
            preferencesEditor.putInt(Preferences.SCHEDULING_COUNT, 0);
            preferencesEditor.apply();
            jobFinished(params, false);
        }, error -> {
            System.out.println("Gagal");
            Log.i("Pengiriman Data", "Gagal " + error);
            notificationGPC.deliverNotification("Pengiriman Data Gagal");
            Calendar calendar = Calendar.getInstance();
            String timestamp = simpleDateFormat.format(calendar.getTime());
            DataModel1 dataModel1 = new DataModel1(timestamp, "Job Scheduler, Tidak Terkirim");
            databaseHelper.addData1(dataModel1);
            jobFinished(params, false);
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}

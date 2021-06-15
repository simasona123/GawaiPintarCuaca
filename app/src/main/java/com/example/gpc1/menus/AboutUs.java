package com.example.gpc1.menus;

import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.gpc1.Preferences;
import com.example.gpc1.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AboutUs extends Activity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = "AboutUsActivity";
    SharedPreferences sharedPreferences;
    Preferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_3);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        TextView linkWeb = (TextView) findViewById(R.id.linkWeb);
        linkWeb.setMovementMethod(LinkMovementMethod.getInstance());
        String linkWebText = "<a href='http://gawaipintarcuaca.online'>Lihat Data</a>";
        if(Build.VERSION.SDK_INT >= 24){
            linkWeb.setText(Html.fromHtml(linkWebText, Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            linkWeb.setText(Html.fromHtml(linkWebText));
        }
        TextView linkStmkg = (TextView) findViewById(R.id.linkStmkg);
        linkStmkg.setMovementMethod(LinkMovementMethod.getInstance());
        String linkStmkgText= "<a href=\"https://stmkg.ac.id/\">STMKG Official</a>";
        if(Build.VERSION.SDK_INT >= 24){
            linkStmkg.setText(Html.fromHtml(linkStmkgText, Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            linkStmkg.setText(Html.fromHtml(linkStmkgText));
        }
        TextView linkEmail = (TextView) findViewById(R.id.linkEmail);
        linkEmail.setMovementMethod(LinkMovementMethod.getInstance());
        sharedPreferences = getSharedPreferences(preferences.SHARED_PRE_FILE, MODE_PRIVATE);
        System.out.println(sharedPreferences.getString(preferences.MODEL, null)+ "  " + sharedPreferences.getString(preferences.VERSION_RELEASE, null));
        String key_UUID = sharedPreferences.getString("key_UUID", null);
        String linkEmailText= "<a href=\"mailto: gawaipintarcuaca@gmail.com?subject= "+ key_UUID +"&body=Tulis Kritik atau Saran" +
                " \">Send Feedback</a>";
        if(Build.VERSION.SDK_INT >= 24){
            linkEmail.setText(Html.fromHtml(linkEmailText, Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            linkEmail.setText(Html.fromHtml(linkEmailText));
        }
        TextView linkGithub = (TextView) findViewById(R.id.linkGithub);
        linkGithub.setMovementMethod(LinkMovementMethod.getInstance());
        String linkGithubText= "<a href=\"https://github.com/simasona123/GawaiPintarCuaca\">Source Code</a>";
        if(Build.VERSION.SDK_INT >= 24){
            linkGithub.setText(Html.fromHtml(linkGithubText, Html.FROM_HTML_MODE_COMPACT));
        }
        else{
            linkGithub.setText(Html.fromHtml(linkGithubText));
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.page_1:
                Intent intent0 = new Intent (this, MainActivity.class);
                startActivity(intent0);
                break;
            case R.id.page_2:
                Intent intent1 = new Intent (this, SensorActivity.class);
                startActivity(intent1);
                break;
            case R.id.page_3:
                break;
        }
        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(LOG_TAG,"On Start");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(LOG_TAG,"On ReStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG,"On Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG,"On Pause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG,"On Stop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG,"On Destroy");
    }
}
package com.example.gpc1.menus;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gpc1.DataModel;
import com.example.gpc1.DatabaseHelper;
import com.example.gpc1.R;

import java.util.ArrayList;


public class LogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ListView listView = findViewById(R.id.dataLog);
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        ArrayList<DataModel> data = databaseHelper.getData();
        CustomAdapter customAdapter = new CustomAdapter(this, data);
        listView.setAdapter(customAdapter);

    }
    public class CustomAdapter extends ArrayAdapter<DataModel>{
        TextView parameter;
        private ArrayList<DataModel> dataList = new ArrayList<>();

        public CustomAdapter(@NonNull Context context, ArrayList<DataModel> data) {
            super(context, R.layout.list_row, data);
            this.dataList = data;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
            }
            DataModel dataModel = dataList.get(position);
            parameter = convertView.findViewById(R.id.data);
            parameter.setText(dataModel.toString());
            return convertView;
        }
    }
}
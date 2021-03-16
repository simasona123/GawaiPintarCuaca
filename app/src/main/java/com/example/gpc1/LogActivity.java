package com.example.gpc1;



import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        public CustomAdapter(@NonNull Context context, ArrayList<DataModel> data) {
            super(context, R.layout.list_row, data);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            DataModel dataModel = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_row, parent, false);
                parameter = convertView.findViewById(R.id.data);
                System.out.println(parameter);
            }
            parameter.setText(dataModel.toString());
            return convertView;
        }
    }
}
package com.example.gpc1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {


    public final String DB_NAME = "gpcDB";
    public static final String TABLE_DB = "gpcTable";
    public final int VERSION_DB = 1;

    public static final String COLUMN_DataID = "DataID";
    public static final String COLUMN_TIME_STAMP = "timestamp";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_ALTITUDE = "altitude";
    private static final String COLUMN_ALTITUDE1 = "altitude1" ;
    public static final String COLUMN_SUHU_UDARA = "suhu_udara";
    public static final String COLUMN_KELEMBABAN_UDARA = "kelembaban_udara";
    public static final String COLUMN_SUHU_BATERAI = "suhu_baterai";
    public static final String COLUMN_TEKANAN_UDARA = "tekanan_udara";
    public static final String COLUMN_CPU_TEMPERATURE = "suhu_cpu";
    public static final String COLUMN_DIKIRIM = "Dikirim";
    public static final String COLUMNSTATUS_LAYAR = "status_layar";
    public static final String COLUMN_STATUS_BATERAI = "status_charging";


    public DatabaseHelper(@Nullable Context context) {
        super(context, "gpcDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_DB + " (" +
                COLUMN_DataID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL, " +
                COLUMN_TIME_STAMP + " TEXT NOT NULL UNIQUE, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_ALTITUDE + " REAL, " +
                COLUMN_ALTITUDE1 + " REAL," +
                COLUMN_SUHU_UDARA + " REAL, " +
                COLUMN_KELEMBABAN_UDARA + " REAL, " +
                COLUMN_SUHU_BATERAI + " REAL, " +
                COLUMN_TEKANAN_UDARA + " REAL, " +
                COLUMN_CPU_TEMPERATURE + " REAL, " +
                COLUMN_DIKIRIM + " BOOLEAN, " +
                COLUMNSTATUS_LAYAR + " BOOLEAN, " +
                COLUMN_STATUS_BATERAI + " BOOLEAN" +
                ");";
        db.execSQL(createTable);
    }

    public boolean addData (DataModel dataModel){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TIME_STAMP, dataModel.getTimeStamp());
        cv.put(COLUMN_LATITUDE, dataModel.getLatitude());
        cv.put(COLUMN_ALTITUDE, dataModel.getAltitude());
        cv.put(COLUMN_LONGITUDE, dataModel.getLongitude());
        cv.put(COLUMN_ALTITUDE1, dataModel.getAltitude1());
        cv.put(COLUMN_SUHU_UDARA, dataModel.getSuhuUdara());
        cv.put(COLUMN_SUHU_BATERAI, dataModel.getSuhuBaterai());
        cv.put(COLUMN_KELEMBABAN_UDARA, dataModel.getKelembabanUdara());
        cv.put(COLUMN_TEKANAN_UDARA, dataModel.getTekananUdara());
        cv.put(COLUMN_CPU_TEMPERATURE, dataModel.getCpuTemperatur());
        cv.put(COLUMN_DIKIRIM, dataModel.isDikirim());
        cv.put(COLUMNSTATUS_LAYAR, dataModel.isStatusLayar());
        cv.put(COLUMN_STATUS_BATERAI, dataModel.isStatusBaterai());
        long insert = db.insert(TABLE_DB, null, cv);
        db.close();
        return insert != -1;
    }

    public ArrayList<DataModel> getData(){
        ArrayList<DataModel> returnList = new ArrayList<>();
        String SQLStatement = "SELECT * FROM " + TABLE_DB + " ORDER BY " + COLUMN_DataID + " DESC LIMIT 20";
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(SQLStatement, null);
        if(cursor.moveToFirst()){
            System.out.println(cursor);
            do{
                int id = cursor.getInt(0);
                String timeStamp = cursor.getString(1);
                float latitude = cursor.getFloat(2);
                float longitude = cursor.getFloat(3);
                float altitude = cursor.getFloat(4);
                float altitude1 = cursor.getFloat(5);
                float suhuUdara = cursor.getFloat(6);
                float kelembabanUdara = cursor.getFloat(7);
                float suhuBaterai = cursor.getFloat(8);
                float tekananUdara = cursor.getFloat(9);
                float cpuTemperatur = cursor.getFloat(10);
                boolean dikirim = cursor.getInt(11) == 1;
                boolean statusLayar = cursor.getInt(12) == 1;
                boolean statusBaterai = cursor.getInt(13) == 1;
                DataModel dataModel = new DataModel(id, timeStamp, latitude, longitude, altitude
                        , altitude1, suhuUdara, kelembabanUdara, suhuBaterai, tekananUdara
                        , cpuTemperatur, dikirim, statusLayar, statusBaterai);
                returnList.add(dataModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    public ArrayList<DataModel> getUnsendingData(){
        ArrayList<DataModel> returnList= new ArrayList<>();
        String sqlQuery = "SELECT * FROM "+ TABLE_DB +" WHERE " + COLUMN_DIKIRIM + " = 0 LIMIT 35;";
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery(sqlQuery, null);
        if(cursor.moveToFirst()){
            System.out.println(cursor);
            do{
                int id = cursor.getInt(0);
                String timeStamp = cursor.getString(1);
                float latitude = cursor.getFloat(2);
                float longitude = cursor.getFloat(3);
                float altitude = cursor.getFloat(4);
                float altitude1 = cursor.getFloat(5);
                float suhuUdara = cursor.getFloat(6);
                float kelembabanUdara = cursor.getFloat(7);
                float suhuBaterai = cursor.getFloat(8);
                float tekananUdara = cursor.getFloat(9);
                float cpuTemperatur = cursor.getFloat(10);
                boolean statusLayar = cursor.getInt(12) == 1;
                boolean statusBaterai = cursor.getInt(13) == 1;
                DataModel dataModel = new DataModel(id, timeStamp, latitude, longitude, altitude
                        , altitude1, suhuUdara, kelembabanUdara, suhuBaterai, tekananUdara
                        , cpuTemperatur, statusLayar, statusBaterai);
                returnList.add(dataModel);
            }while(cursor.moveToNext());
        }
        cursor.close();
        return returnList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean updateStatusKirim(DataModel dataModel){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_DataID, dataModel.getId());
        cv.put(COLUMN_TIME_STAMP, dataModel.getTimeStamp());
        cv.put(COLUMN_LATITUDE, dataModel.getLatitude());
        cv.put(COLUMN_ALTITUDE, dataModel.getAltitude());
        cv.put(COLUMN_LONGITUDE, dataModel.getLongitude());
        cv.put(COLUMN_ALTITUDE1, dataModel.getAltitude1());
        cv.put(COLUMN_SUHU_UDARA, dataModel.getSuhuUdara());
        cv.put(COLUMN_SUHU_BATERAI, dataModel.getSuhuBaterai());
        cv.put(COLUMN_KELEMBABAN_UDARA, dataModel.getKelembabanUdara());
        cv.put(COLUMN_TEKANAN_UDARA, dataModel.getTekananUdara());
        cv.put(COLUMN_CPU_TEMPERATURE, dataModel.getCpuTemperatur());
        cv.put(COLUMN_DIKIRIM, 1);
        cv.put(COLUMNSTATUS_LAYAR, dataModel.isStatusLayar());
        cv.put(COLUMN_STATUS_BATERAI, dataModel.isStatusBaterai());
        return db.update(TABLE_DB, cv, COLUMN_DataID + " = " + dataModel.getId(), null) > 0;
    }

}

package com.example.gpc1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COLUMN_DataID = "DataID";
    public static final String COLUMN_TIME_STAMP = "TimeStamp";
    public static final String COLUMN_LATITUDE = "Latitude";
    public static final String COLUMN_LONGITUDE = "Longitude";
    public static final String COLUMN_ALTITUDE = "Altitude";
    public static final String COLUMN_SUHU_UDARA = "SuhuUdara";
    public static final String COLUMN_KELEMBABAN_UDARA = "KelembabanUdara";
    public static final String COLUMN_SUHU_BATERAI = "SuhuBaterai";
    public static final String COLUMN_TEKANAN_UDARA = "TekananUdara";
    public static final String COLUMN_CPU_TEMPERATURE = "CpuTemperature";
    public static final String COLUMN_DIKIRIM = "Dikirim";
    public static final String COLUMNSTATUS_LAYAR = "StatusLayar";
    public static final String COLUMN_STATUS_BATERAI = "StatusBaterai";
    public final String dbName = "gpcDB";
    public final int versionDB = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, "gpcDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS gpcDB.gpcTable (" +
                COLUMN_DataID + " INTEGER PRIMARY KEY AUTOINCREMENT  NOT NULL, " +
                COLUMN_TIME_STAMP + " TEXT NOT NULL UNIQUE, " +
                COLUMN_LATITUDE + " REAL, " +
                COLUMN_LONGITUDE + " REAL, " +
                COLUMN_ALTITUDE + " REAL, " +
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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

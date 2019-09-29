package com.example.preloaddata.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import static android.provider.BaseColumns._ID;
import static com.example.preloaddata.database.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.preloaddata.database.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.preloaddata.database.DatabaseContract.TABLE_NAME;


// call SqliteHelper
public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DATABASE_NAME= "Mahasiswa.db";
    private static final int DATABASE_VERSION=1;
    // create mahasiswa
    private static String CREATE_TABLE_MAHASISWA= " create table "+ TABLE_NAME +
            " (" + _ID + " integer primary key autoincrement, " +
            NAMA + " text not null, " +
            NIM + " text not null); " ;
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // create mahasiswa
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MAHASISWA);
    }


    // delete mahasiswa
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }
}

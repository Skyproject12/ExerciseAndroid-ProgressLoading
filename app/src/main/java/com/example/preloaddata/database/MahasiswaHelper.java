package com.example.preloaddata.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.preloaddata.database.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.preloaddata.database.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.preloaddata.database.DatabaseContract.TABLE_NAME;

public class MahasiswaHelper {


    // call database helper
    private DatabaseHelper databaseHelper;
    private static MahasiswaHelper INSTANCE;
    private SQLiteDatabase database;
    public MahasiswaHelper (Context context){
        databaseHelper= new DatabaseHelper(context);
    }

    // call instance
    public static MahasiswaHelper getInstance(Context context){
        if (INSTANCE == null){
            synchronized (SQLiteOpenHelper.class){

                // check instance null or not
                if (INSTANCE == null){
                    INSTANCE= new MahasiswaHelper(context);
                }
            }
        }

        // return instance
        return INSTANCE;
    }


    // to write sqlite
    public void open() throws SQLException {
        database= databaseHelper.getWritableDatabase();
    }


    // to close sqlite helper if open
    public void close() {
        databaseHelper.close();
        if(database.isOpen()){
            database.close();
        }
    }


    // get all from sqlite
    public ArrayList<MahasiswaModel> getAllData() {
        Cursor cursor= database.query(TABLE_NAME, null, null, null, null, null, _ID + " ASC ", null);
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList= new ArrayList<>();
        MahasiswaModel mahasiswaModel;

        if(cursor.getCount() >0){
            do {
                mahasiswaModel= new MahasiswaModel();
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                arrayList.add(mahasiswaModel);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        cursor.close();
        // save in arraylist
        return arrayList;
    }

    // isnert from sqlite
    public long insert(MahasiswaModel mahasiswaModel){
        ContentValues contentValues= new ContentValues();
        contentValues.put(NAMA, mahasiswaModel.getName());
        contentValues.put(NIM, mahasiswaModel.getNim());
        return database.insert(TABLE_NAME, null, contentValues);
    }

    // get by name
    public ArrayList<MahasiswaModel> getDataByName(String nama){
        Cursor cursor= database.query(TABLE_NAME, null, NAMA + " LIKE ? ", new String[]{nama}, null, null, _ID + " ASC ", null);
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList= new ArrayList<>();
        MahasiswaModel mahasiswaModel;
        if(cursor.getCount() > 0){
            do {
                mahasiswaModel= new MahasiswaModel();
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                arrayList.add(mahasiswaModel);
                cursor.moveToNext();
            }while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }
}

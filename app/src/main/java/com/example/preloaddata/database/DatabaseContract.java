package com.example.preloaddata.database;

import android.provider.BaseColumns;


// membuat table nama
public class DatabaseContract {
    static String TABLE_NAME="mahasiswa";


    // mmebuat column
    static final class MahasiswaColumns implements BaseColumns{
        static String NAMA= "nama";
        static String NIM= "nim";
    }
}

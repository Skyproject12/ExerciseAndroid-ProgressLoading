package com.example.preloaddata.database;

import android.provider.BaseColumns;

public class DatabaseContract {
    static String TABLE_NAME="mahasiswa";

    static final class MahasiswaColumns implements BaseColumns{
        static String NAMA= "nama";
        static String NIM= "nim";
    }
}

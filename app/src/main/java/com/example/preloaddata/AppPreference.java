package com.example.preloaddata;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    private static final String PREFS_NAME= "MahasiswaPref";
    private static final String APP_FIRST_RUN= "app_first_run";
    private SharedPreferences prefs;

    public AppPreference(Context context){
        prefs= context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    // menampung ke dalam prepernces status load progress
    public void setFirstRun(Boolean input){
        // menentukan status apakah sudah menampung hasil file atau belum
        SharedPreferences.Editor editor= prefs.edit();
        editor.putBoolean(APP_FIRST_RUN, input);
        editor.apply();
    }

    public Boolean getFirstRun() {
        return prefs.getBoolean(APP_FIRST_RUN, true);
    }
}

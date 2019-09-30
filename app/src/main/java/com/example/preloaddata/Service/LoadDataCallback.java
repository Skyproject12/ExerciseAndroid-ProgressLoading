package com.example.preloaddata.Service;

public interface LoadDataCallback {

    void onPreload();
    void onProgressUpdate(long progress);
    void onLoadSuccess();
    void onLoadFailed();
    void onLoadCancel();
}

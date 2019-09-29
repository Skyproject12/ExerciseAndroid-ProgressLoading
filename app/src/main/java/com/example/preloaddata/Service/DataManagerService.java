package com.example.preloaddata.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.example.preloaddata.AppPreference;
import com.example.preloaddata.database.MahasiswaHelper;

public class DataManagerService extends Service {

    public static final int PREPARATION_MESSAGE=0;
    public static final int UPDATE_MESSAGE=1;
    public final int SUCCESS_MESSAGE=2;
    public static final int FAILED_MESSAGE=3;
    public static final String ACTIVITY_HANDLER="activity_handler";

    private String TAG= DataManagerService.class.getSimpleName();
    private LoadDataAsync loadData;
    private Messenger activityMessanger;
    public DataManagerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // getParcable from messanger
        activityMessanger= intent.getParcelableExtra(ACTIVITY_HANDLER);

        // jalankan  asyntask
        loadData.execute(); // akan dijalankan ketika activity sudah mengikat service

        // kembalikan nilai message
        return activityMessanger.getBinder();
    }

    // call interface
    LoadDataCallback callback= new LoadDataCallback() {

        // if method onPreload
        @Override
        public void onPreload() {

            // prepare load status
            Message message= Message.obtain(null, PREPARATION_MESSAGE);
            try {

                // send status message
                activityMessanger.send(message);;
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }


        // update message
        @Override
        public void onProgressUpdate(long progress) {
            try {
                Message message= Message.obtain(null, UPDATE_MESSAGE);
                Bundle bundle= new Bundle();
                // put extra bundle progress
                bundle.putLong("KEY_PROGRESS", progress);
                message.setData(bundle);
                // send status message
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }


        // success message
        @Override
        public void onLoadSuccess() {
            Message message= Message.obtain(null, SUCCESS_MESSAGE);
            try {

                // send status message
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }


        // failed message
        @Override
        public void onLoadFailed() {
            Message message= Message.obtain(null, FAILED_MESSAGE);
            try {

                //send message
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }
    };


    // in oncreate
    @Override
    public void onCreate() {
        super.onCreate();
        // berfungsi mengambil kelas helper maupun preference yang nantinya akan diperbarui untuk di check kembali di async
        MahasiswaHelper mahasiswaHelper= MahasiswaHelper.getInstance(getApplicationContext());
        AppPreference appPreference= new AppPreference(getApplicationContext());

        // get mahasiswahelper , prefernce
        loadData= new LoadDataAsync(mahasiswaHelper, appPreference, callback, getResources());
        Log.d(TAG, "onCreate: ");
    }


    // destroy service
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}

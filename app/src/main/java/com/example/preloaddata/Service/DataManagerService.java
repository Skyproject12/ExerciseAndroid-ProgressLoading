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
        activityMessanger= intent.getParcelableExtra(ACTIVITY_HANDLER);
        loadData.execute();
        return activityMessanger.getBinder();
    }
    LoadDataCallback callback= new LoadDataCallback() {
        @Override
        public void onPreload() {
            Message message= Message.obtain(null, PREPARATION_MESSAGE);
            try {
                activityMessanger.send(message);;
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onProgressUpdate(long progress) {
            try {
                Message message= Message.obtain(null, UPDATE_MESSAGE);
                Bundle bundle= new Bundle();
                bundle.putLong("KEY_PROGRESS", progress);
                message.setData(bundle);
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadSuccess() {
            Message message= Message.obtain(null, SUCCESS_MESSAGE);
            try {
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }

        @Override
        public void onLoadFailed() {
            Message message= Message.obtain(null, FAILED_MESSAGE);
            try {
                activityMessanger.send(message);
            }
            catch (RemoteException e){
                e.printStackTrace();
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        MahasiswaHelper mahasiswaHelper= MahasiswaHelper.getInstance(getApplicationContext());
        AppPreference appPreference= new AppPreference(getApplicationContext());
        loadData= new LoadDataAsync(mahasiswaHelper, appPreference, callback, getResources());
        Log.d(TAG, "onCreate: ");
    }

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

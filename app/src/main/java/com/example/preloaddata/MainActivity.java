package com.example.preloaddata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.preloaddata.Service.DataManagerService;

import java.lang.ref.WeakReference;

import static com.example.preloaddata.Service.DataManagerService.FAILED_MESSAGE;
import static com.example.preloaddata.Service.DataManagerService.PREPARATION_MESSAGE;
import static com.example.preloaddata.Service.DataManagerService.UPDATE_MESSAGE;

public class MainActivity extends AppCompatActivity implements HandlerCallback {

    ProgressBar progressBar;
    Messenger activityMessage;

    Messenger boundService;
    boolean serviceBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar= findViewById(R.id.progress_bar);

        Intent intent= new Intent(MainActivity.this, DataManagerService.class);
        activityMessage= new Messenger(new IncomingHandler(this));
        intent.putExtra(DataManagerService.ACTIVITY_HANDLER, activityMessage);

        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection= new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boundService= new Messenger(service);
            serviceBound= true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound= false;
        }
    };

    @Override
    public void preparation() {
        Toast.makeText(this, "Memulai memuat data", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProgress(long progress) {
        progressBar.setProgress((int) progress);
    }

    @Override
    public void loadSucces() {
        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, MahasiswaActivity.class));
        finish();
    }

    @Override
    public void loadFailed() {
        Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
    }
    private static class IncomingHandler extends Handler {
        WeakReference<HandlerCallback> weakCallback;
        IncomingHandler(HandlerCallback callback){
            weakCallback= new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case PREPARATION_MESSAGE:
                    weakCallback.get().preparation();
                    break;
                case UPDATE_MESSAGE:
                    Bundle bundle= msg.getData();
                    long progress= bundle.getLong("KEY_PROGRESS");
                    weakCallback.get().updateProgress(progress);
                    break;
                case 2:
                    weakCallback.get().loadSucces();
                    break;
                case FAILED_MESSAGE :
                    weakCallback.get().loadFailed();
                    break;
            }
        }
    }
}

interface HandlerCallback{
    void preparation();
    void updateProgress(long progress);
    void loadSucces();
    void loadFailed();
}

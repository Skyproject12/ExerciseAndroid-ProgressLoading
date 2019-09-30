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

import static com.example.preloaddata.Service.DataManagerService.CANCEL_MESSAGE;
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


        // mengirim status progress
        Intent intent= new Intent(MainActivity.this, DataManagerService.class);
        activityMessage= new Messenger(new IncomingHandler(this));
        intent.putExtra(DataManagerService.ACTIVITY_HANDLER, activityMessage);


        // menjalankan service
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    // menghancurkan service ketika selesai
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }


    // melakukan pengecekan koneksi
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


    // ketika proses akan dimulai
    @Override
    public void preparation() {
        Toast.makeText(this, "Memulai memuat data", Toast.LENGTH_SHORT).show();
    }


    // ketika proses akan diupdate
    @Override
    public void updateProgress(long progress) {
        progressBar.setProgress((int) progress);
    }


    // ketika proses success
    @Override
    public void loadSucces() {
        Toast.makeText(this, "Berhasil", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, MahasiswaActivity.class));
        finish();
    }


    // ketika proses failed
    @Override
    public void loadFailed() {
        Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loadCancel() {
        Toast.makeText(this, "load cancel", Toast.LENGTH_SHORT).show();
    }

    private static class IncomingHandler extends Handler {
        WeakReference<HandlerCallback> weakCallback;
        IncomingHandler(HandlerCallback callback){
            weakCallback= new WeakReference<>(callback);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            // menerima message dari service melalui media messanger
            switch (msg.what){

                // ketika status message PREPARE
                case PREPARATION_MESSAGE:
                    weakCallback.get().preparation();
                    break;

                    // ketika status message update
                case UPDATE_MESSAGE:
                    Bundle bundle= msg.getData();
                    long progress= bundle.getLong("KEY_PROGRESS");
                    weakCallback.get().updateProgress(progress);
                    break;

                    // ketika status message success
                case 2:
                    weakCallback.get().loadSucces();
                    break;

                    // ketika status message failed
                case FAILED_MESSAGE :
                    weakCallback.get().loadFailed();
                    break;
                    // berfungsi untuk menampilkan response ketika status CANCEL
                case CANCEL_MESSAGE:
                    // menampilkan response berdasarkan interface
                    weakCallback.get().loadCancel();
                    break;
            }
        }
    }
}


// menangkap hasil status dari service
interface HandlerCallback{
    void preparation();
    void updateProgress(long progress);
    void loadSucces();
    void loadFailed();
    void loadCancel();
}

package com.example.preloaddata.Service;

import android.content.res.Resources;
import android.os.AsyncTask;

import com.example.preloaddata.AppPreference;
import com.example.preloaddata.R;
import com.example.preloaddata.database.MahasiswaHelper;
import com.example.preloaddata.database.MahasiswaModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class LoadDataAsync extends AsyncTask<Void, Integer, Boolean> {

    private final String TAG= LoadDataAsync.class.getSimpleName();
    private MahasiswaHelper mahasiswaHelper;
    private AppPreference appPreference;
    // ketika akan mengunkan interface loadDatacallback
    private WeakReference<LoadDataCallback> weakCallback;
    // digunakan ketika akn menggunakan resource
    private WeakReference<Resources> weakResounrce;
    double progress;
    double maxprogress= 100;

    public LoadDataAsync(MahasiswaHelper mahasiswaHelper, AppPreference appPreference, LoadDataCallback weakCallback, Resources weakResounrce) {
        this.mahasiswaHelper = mahasiswaHelper;
        this.appPreference = appPreference;
        this.weakCallback = new WeakReference<>(weakCallback);
        this.weakResounrce = new WeakReference<>(weakResounrce);
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean firstRun= appPreference.getFirstRun();
        if(firstRun){
            ArrayList<MahasiswaModel> mahasiswaModels= preLoadRaw();
            mahasiswaHelper.open();

            progress= 30;
            publishProgress((int)progress);
            Double progressMaxInsert= 80.0;
            Double progressDiff= (progressMaxInsert - progress) / mahasiswaModels.size();
            boolean insertSuccess;
            try {
                for(MahasiswaModel model: mahasiswaModels){
                    mahasiswaHelper.insert(model);
                    progress += progressDiff;
                    publishProgress((int) progress);
                }
                insertSuccess= true;
                appPreference.setFirstRun(false);
            }
            catch (Exception e){
                insertSuccess= false;
            }
            mahasiswaHelper.close();
            publishProgress((int)maxprogress);
            return insertSuccess;
        }
        else{
            try {
                synchronized (this) {
                    this.wait(2000);
                    publishProgress(50);
                    this.wait(2000);
                    publishProgress((int)maxprogress);
                    return true;
                }
            }
            catch (Exception e){
                return false;
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){
            weakCallback.get().onLoadSuccess();
        }
        else {
            weakCallback.get().onLoadFailed();
        }
    }

    private ArrayList<MahasiswaModel> preLoadRaw() {
        ArrayList<MahasiswaModel> mahasiswaModels= new ArrayList<>();
        String line;
        BufferedReader reader;
        try {
            Resources resources= weakResounrce.get();
            // berfungsi mengakses dan menulis file di android
            InputStream raw_dict= resources.openRawResource(R.raw.data_mahasiswa);
            reader= new BufferedReader(new InputStreamReader(raw_dict));
            do {
                line= reader.readLine();
                String [] spliststr= line.split("\t");
                MahasiswaModel mahasiswaModel;
                mahasiswaModel= new MahasiswaModel(spliststr[0], spliststr[1]);
                mahasiswaModels.add(mahasiswaModel);
            }while (line != null);
        }catch (Exception e){
            e.printStackTrace();
        }
        return mahasiswaModels;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // call interface with method onPreload
        weakCallback.get().onPreload();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // call interface with method onPreload
        weakCallback.get().onProgressUpdate(values[0]);
    }
}

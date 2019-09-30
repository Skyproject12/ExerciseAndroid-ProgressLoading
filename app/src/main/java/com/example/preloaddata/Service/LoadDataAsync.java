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
    private WeakReference<LoadDataCallback> weakCallback; // weakreference berfungsi menghubungkan service dengan suatu data
    // digunakan ketika akn menggunakan resource
    private WeakReference<Resources> weakResounrce;
    // give status progress
    double progress;
    double maxprogress= 100;


    // make cosntructor
    public LoadDataAsync(MahasiswaHelper mahasiswaHelper, AppPreference appPreference, LoadDataCallback weakCallback, Resources weakResounrce) {
        // weakreference akan memperbarui data sesuai dengan inputan
        this.mahasiswaHelper = mahasiswaHelper;
        this.appPreference = appPreference;
        this.weakCallback = new WeakReference<>(weakCallback);
        this.weakResounrce = new WeakReference<>(weakResounrce);
    }


    // proses loading
    @Override
    protected Boolean doInBackground(Void... voids) {
        Boolean firstRun= appPreference.getFirstRun();

        // jika first run true maka akan melakukan insert ke dalam databse
        if(firstRun){

            // make arraylist type MahasiswaModel
            ArrayList<MahasiswaModel> mahasiswaModels= preLoadRaw();
            mahasiswaHelper.open();


            // make progress 30
            progress= 30;
            publishProgress((int)progress);
            Double progressMaxInsert= 80.0;

            // give change progress
            Double progressDiff= (progressMaxInsert - progress) / mahasiswaModels.size(); // akan membuat progress bertambah sebanyak jumlah dari arraylist
            boolean insertSuccess;
            try {
                // memanggil kode untuk menerima treansaction
                mahasiswaHelper.beginTransaction();
                // melakukan perulangan ketika memasukkan ke sqlite
                for(MahasiswaModel model: mahasiswaModels){

                    if(isCancelled()){
                        // ketika menjalankan proses namun pengguna membatalkan proses
                        weakCallback.get().onLoadCancel();
                        break;
                    }
                    else {
                        // memasukkan ke dalam sqlite menggunakan insertTransaction
                        mahasiswaHelper.insertTransaction(model);

                        // setiap melakukan progress akan menambah progressdiff
                        progress += progressDiff;
                        // menampilka  progress
                        publishProgress((int) progress);
                    }
                }
                if (isCancelled()){
                    insertSuccess=false;
                    // mengeset staus first run true
                    appPreference.setFirstRun(true);
                    weakCallback.get().onLoadCancel();
                }
                else {
                    mahasiswaHelper.setTransactionSuccess();
                    insertSuccess = true;
                    // ketika success insert
                    appPreference.setFirstRun(false);
                }
            }
            catch (Exception e){
                insertSuccess= false;
            }
            finally {
                mahasiswaHelper.endTransaction();
            }

            // menutup helper
            mahasiswaHelper.close();

            // tampilkan progress seratus persen
            publishProgress((int)maxprogress);

            // membuat status insert true
            return insertSuccess;
        }

        // selain hal tersebut
        else{
            // hanya akan melakukan proses loading
            try {
                synchronized (this) {

                    // menunggu dua detik
                    this.wait(2000);

                    // menmpilkan progress limapuluh persen
                    publishProgress(50);
                    this.wait(2000);

                    // menuggul lalu menampikan progress seartus persen
                    publishProgress((int)maxprogress);
                    return true;
                }
            }
            catch (Exception e){
                return false;
            }
        }
    }


    // menjalalankan proses  dengan response tertentu
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if(aBoolean){

            // load success maka akan memanggil interface onLoadSuccess
            weakCallback.get().onLoadSuccess();
        }
        else {

            // jika failed akan memanggil interface  onLoadFailed
            weakCallback.get().onLoadFailed();
        }
    }


    // berfunsgi untuk mengambil file dari dalam android
    private ArrayList<MahasiswaModel> preLoadRaw() {
        ArrayList<MahasiswaModel> mahasiswaModels= new ArrayList<>();
        String line;
        BufferedReader reader;
        try {
            Resources resources= weakResounrce.get();
            // berfungsi mengakses dan menulis file di android
            InputStream raw_dict= resources.openRawResource(R.raw.data_mahasiswa);
            // mengambil data_mahasiswa kemudian akan di parse
            reader= new BufferedReader(new InputStreamReader(raw_dict));
            do {
                line= reader.readLine();
                // mengamnbil dan mebaca data berdasarkan tab dari data
                String [] spliststr= line.split("\t");
                // nilai nama dan id akan di gunakan untuk data dalam mahassiwamodel
                MahasiswaModel mahasiswaModel;
                mahasiswaModel= new MahasiswaModel(spliststr[0], spliststr[1]);
                // menampung file ke dalam arraylist
                mahasiswaModels.add(mahasiswaModel);
            }while (line != null);
        }catch (Exception e){
            e.printStackTrace();
        }

        // mengembalikan nilai dari arraylist
        return mahasiswaModels;
    }


    // melakukan persiapan exsekusi
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // call interface with method onPreload
        weakCallback.get().onPreload();
    }


    // melakukan update data terbaru
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // call interface with method onPreload
        weakCallback.get().onProgressUpdate(values[0]);
    }
}

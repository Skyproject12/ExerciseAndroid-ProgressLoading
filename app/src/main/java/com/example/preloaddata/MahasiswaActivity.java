package com.example.preloaddata;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.preloaddata.database.MahasiswaHelper;
import com.example.preloaddata.database.MahasiswaModel;

import java.util.ArrayList;

public class MahasiswaActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    MahasiswaAdapter mahasiswaAdapter;
    MahasiswaHelper mahasiswaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);

        recyclerView= findViewById(R.id.recyclerview);
        mahasiswaHelper= new MahasiswaHelper(this);
        mahasiswaAdapter= new MahasiswaAdapter();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mahasiswaAdapter);
        mahasiswaHelper.open();
        ArrayList<MahasiswaModel> mahasiswaModels= mahasiswaHelper.getAllData();
        mahasiswaHelper.close();


        // set adapter mahasiswamodel arraylist
        mahasiswaAdapter.setData(mahasiswaModels);
    }
}

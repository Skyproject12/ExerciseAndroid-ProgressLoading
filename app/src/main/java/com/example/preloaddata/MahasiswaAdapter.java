package com.example.preloaddata;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.preloaddata.database.MahasiswaModel;

import java.util.ArrayList;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.MahasiswaHolder> {

    private ArrayList<MahasiswaModel> list= new ArrayList<>();

    public MahasiswaAdapter() {
    }

    public void setData(ArrayList<MahasiswaModel> list){
        if (list.size() >0){
            this.list.clear();
        }
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MahasiswaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mahasiswa_row, parent, false);
        return new MahasiswaHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MahasiswaHolder holder, int position) {
        holder.textNama.setText(list.get(position).getName());
        holder.textNim.setText(list.get(position).getNim());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MahasiswaHolder extends RecyclerView.ViewHolder {
        private TextView textNim;
        private TextView textNama;
        public MahasiswaHolder(@NonNull View itemView) {
            super(itemView);
            textNim= itemView.findViewById(R.id.text_nim);
            textNama= itemView.findViewById(R.id.text_nama);
        }
    }
}

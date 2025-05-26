package com.example.drivein;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    Context context;
    ArrayList<ParkingModel> list;

    public MyAdapter(Context context, ArrayList<ParkingModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.vehicle_enrty, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ParkingModel parking = list.get(position);
        holder.plate.setText(parking.getPlate());
        holder.start_time.setText(parking.getStartTime());
        holder.duration.setText(parking.getDuration());
        holder.status.setText(parking.getStatus());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView plate, start_time, duration, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            plate = itemView.findViewById(R.id.txtPlate);
            start_time = itemView.findViewById(R.id.txtStartTime);
            duration = itemView.findViewById(R.id.txtDuration);
            status = itemView.findViewById(R.id.txtStatus);
        }
    }
}

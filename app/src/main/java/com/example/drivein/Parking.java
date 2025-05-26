package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Parking extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<ParkingModel> list;
    private DatabaseReference databaseReference;
    private MyAdapter adapter;
    private TextView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking);

        recyclerView = findViewById(R.id.recycleView);
        btnBack = findViewById(R.id.btnBack);

        list = new ArrayList<>();
        adapter = new MyAdapter(this, list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("current_status");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ParkingModel parking = dataSnapshot.getValue(ParkingModel.class);
                    if (parking != null) {
                        list.add(parking);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optional: show error
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent backIntent = new Intent(Parking.this, Home.class);
            startActivity(backIntent);
            finish();
        });
    }
}

package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        navigation();


    }

    public void navigation(){
        LinearLayout btnMyVehicle = findViewById(R.id.btnParking);
        btnMyVehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myVehiclesIntent = new Intent(getApplicationContext(), Parking.class);
                startActivity(myVehiclesIntent);
                finish();
            }
        });

        LinearLayout btnNewBooking = findViewById(R.id.btnParkingHistory);
        btnNewBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newBookingIntent = new Intent(getApplicationContext(), ParkingHistory.class);
                startActivity(newBookingIntent);
                finish();
            }
        });


    }
}
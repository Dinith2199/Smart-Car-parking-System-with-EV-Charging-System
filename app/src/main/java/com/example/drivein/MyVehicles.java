package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MyVehicles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_vehicles);

        navigation();

    }

    public void navigation(){
        LinearLayout btnAddNew = findViewById(R.id.btnAddNewVehicle);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addNewVehicleIntent = new Intent(getApplicationContext(), Add_Vehicle_Form.class);
                startActivity(addNewVehicleIntent);
                finish();
            }
        });

        LinearLayout btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btnBackIntent = new Intent(getApplicationContext(), Home.class);
                startActivity(btnBackIntent);
                finish();
            }
        });
    }
}
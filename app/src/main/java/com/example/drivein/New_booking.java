package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class New_booking extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_booking);

        navigation();

    }

    public void navigation(){
        Button btnSave = findViewById(R.id.btnNewBookingSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent btnSaveIntent = new Intent(getApplicationContext(), Book_Confirmed.class);
                startActivity(btnSaveIntent);
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
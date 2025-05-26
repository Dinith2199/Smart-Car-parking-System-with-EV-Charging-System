package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //Logo Loading delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent LoadingIntent = new Intent(getApplicationContext(), Loading_activity.class);
                startActivity(LoadingIntent);
            }
        }, 3000);//delay time 3sec

    }
}
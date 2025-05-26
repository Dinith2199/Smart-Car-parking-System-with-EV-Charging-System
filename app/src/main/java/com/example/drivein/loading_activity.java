package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Loading_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);

        //Loading Delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loadingINtent2 = new Intent(Loading_activity.this, Login.class);
                startActivity(loadingINtent2);
                finish();
            }
        },3000);


    }
}
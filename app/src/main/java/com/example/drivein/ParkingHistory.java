package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ParkingHistory extends AppCompatActivity {

    private LinearLayout parkingHistoryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parking_history);

        parkingHistoryContainer = findViewById(R.id.parkingHistoryContainer);

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://nodemcu-55278-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference parkingHistoryRef = database.getReference("parking_history/slot1");

        // Fetch data from Firebase
        parkingHistoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                parkingHistoryContainer.removeAllViews(); // Clear previous views
                for (DataSnapshot entrySnapshot : dataSnapshot.getChildren()) {
                    String duration = entrySnapshot.child("duration").getValue(String.class);
                    String endTime = entrySnapshot.child("end_time").getValue(String.class);
                    String numberPlate = entrySnapshot.child("number_plate").getValue(String.class);
                    String startTime = entrySnapshot.child("start_time").getValue(String.class);

                    // Dynamically create a view for each parking history entry
                    addParkingEntryView(duration, endTime, numberPlate, startTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
                TextView errorText = new TextView(ParkingHistory.this);
                errorText.setText("Error fetching data: " + databaseError.getMessage());
                errorText.setTextColor(getResources().getColor(android.R.color.white));
                parkingHistoryContainer.addView(errorText);
            }
        });

        navigation();
    }

    private void addParkingEntryView(String duration, String endTime, String numberPlate, String startTime) {
        // Inflate a new view for each parking entry
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout entryLayout = new LinearLayout(this);
        entryLayout.setOrientation(LinearLayout.VERTICAL);
        entryLayout.setPadding(0, 20, 0, 20);

        // Duration
        LinearLayout durationLayout = new LinearLayout(this);
        durationLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView durationLabel = new TextView(this);
        durationLabel.setText("Duration: ");
        durationLabel.setTextColor(getResources().getColor(android.R.color.white));
        durationLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView durationValue = new TextView(this);
        durationValue.setText(duration);
        durationValue.setTextColor(getResources().getColor(android.R.color.white));
        durationLayout.addView(durationLabel);
        durationLayout.addView(durationValue);

        // End Time
        LinearLayout endTimeLayout = new LinearLayout(this);
        endTimeLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView endTimeLabel = new TextView(this);
        endTimeLabel.setText("End Time: ");
        endTimeLabel.setTextColor(getResources().getColor(android.R.color.white));
        endTimeLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView endTimeValue = new TextView(this);
        endTimeValue.setText(endTime);
        endTimeValue.setTextColor(getResources().getColor(android.R.color.white));
        endTimeLayout.addView(endTimeLabel);
        endTimeLayout.addView(endTimeValue);

        // Number Plate
        LinearLayout numberPlateLayout = new LinearLayout(this);
        numberPlateLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView numberPlateLabel = new TextView(this);
        numberPlateLabel.setText("Number Plate: ");
        numberPlateLabel.setTextColor(getResources().getColor(android.R.color.white));
        numberPlateLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView numberPlateValue = new TextView(this);
        numberPlateValue.setText(numberPlate);
        numberPlateValue.setTextColor(getResources().getColor(android.R.color.white));
        numberPlateLayout.addView(numberPlateLabel);
        numberPlateLayout.addView(numberPlateValue);

        // Start Time
        LinearLayout startTimeLayout = new LinearLayout(this);
        startTimeLayout.setOrientation(LinearLayout.HORIZONTAL);
        TextView startTimeLabel = new TextView(this);
        startTimeLabel.setText("Start Time: ");
        startTimeLabel.setTextColor(getResources().getColor(android.R.color.white));
        startTimeLabel.setTypeface(null, android.graphics.Typeface.BOLD);
        TextView startTimeValue = new TextView(this);
        startTimeValue.setText(startTime);
        startTimeValue.setTextColor(getResources().getColor(android.R.color.white));
        startTimeLayout.addView(startTimeLabel);
        startTimeLayout.addView(startTimeValue);

        // Add all to the entry layout
        entryLayout.addView(durationLayout);
        entryLayout.addView(endTimeLayout);
        entryLayout.addView(numberPlateLayout);
        entryLayout.addView(startTimeLayout);

        // Add a divider
        View divider = new View(this);
        divider.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divider.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));

        parkingHistoryContainer.addView(entryLayout);
        parkingHistoryContainer.addView(divider);
    }

    public void navigation() {
        TextView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(getApplicationContext(), Home.class);
            startActivity(backIntent);
            finish();
        });
    }
}
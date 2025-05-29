package com.example.drivein;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Home extends AppCompatActivity {

    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        navigation();
        displayNumberPlate();
        setupSpinner();
        asignSlotUpdate();
        gateOpen();

    }

    public void gateOpen() {
        // Find the Switch by its ID
        Switch btnGate = findViewById(R.id.btnGate);

        // Initialize the Firebase Database reference
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("gate/status");

        // Set an OnCheckedChangeListener to handle Switch toggles
        btnGate.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Update Firebase based on Switch state
            databaseReference.setValue(isChecked).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    // If the Firebase update fails, revert the Switch state
                    btnGate.setOnCheckedChangeListener(null); // Temporarily disable listener to avoid loop
                    btnGate.setChecked(!isChecked); // Revert to previous state
                     // Re-enable listener
                }
            });
        });

        // Optionally, fetch the initial state from Firebase to set the Switch
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean gateStatus = snapshot.getValue(Boolean.class);
                if (gateStatus != null) {
                    btnGate.setChecked(gateStatus);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error (e.g., set Switch to default state)
                btnGate.setChecked(false);
            }
        });
    }



    private Spinner spinnerSlots; // Declare Spinner as a class variable to access in both methods

    public void setupSpinner() {
        // Find the Spinner by its ID
        spinnerSlots = findViewById(R.id.slotSpinner);

        // Create a list of slot options
        String[] slots = {"Slot 1", "Slot 2", "Slot 3", "Slot 4"};

        // Create an ArrayAdapter to populate the Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, // Context (Activity)
                android.R.layout.simple_spinner_item, // Default layout for Spinner items
                slots // Array of slot options
        );

        // Set the dropdown view layout
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attach the adapter to the Spinner
        spinnerSlots.setAdapter(adapter);
    }

    public void asignSlotUpdate() {
        // Find the TextView by its ID
        TextView text = findViewById(R.id.txtNumberPlate);

        // Find the Button by its ID
        Button btnConfirm = findViewById(R.id.btnConfirmSlot);

        // Set the OnClickListener for the button
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the selected slot from the Spinner
                String selectedSlot = spinnerSlots.getSelectedItem().toString();

                // Map the Spinner selection to Firebase slot path (e.g., "Slot 1" -> "slot1")
                String slotKey;
                switch (selectedSlot) {
                    case "Slot 1":
                        slotKey = "slot1";
                        break;
                    case "Slot 2":
                        slotKey = "slot2";
                        break;
                    case "Slot 3":
                        slotKey = "slot3";
                        break;
                    case "Slot 4":
                        slotKey = "slot4";
                        break;
                    default:
                        text.setText("Invalid slot selection");
                        return;
                }

                // Initialize the Firebase Database reference for the selected slot
                DatabaseReference slotRef = FirebaseDatabase.getInstance().getReference("current_status/" + slotKey);

                // Get the text from the TextView and trim it
                String numberPlate = text.getText().toString().trim();

                // Validate the input
                if (numberPlate.isEmpty()) {
                    text.setText("No Vehicles");
                    return;
                }

                // Create a map to update multiple fields
                Map<String, Object> updates = new HashMap<>();
                updates.put("plate", numberPlate); // Update the plate
                updates.put("status", "occupied"); // Update status to occupied
                updates.put("start_time", "03:25 AM"); // Use current time (May 29, 2025, 03:25 AM)
                updates.put("free_led_status", false); // Turn off free LED status
                updates.put("led_status", true); // Turn on LED status to indicate occupancy

                // Update the slot in Firebase
                slotRef.updateChildren(updates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Optional: Update UI or log success
                        text.setText(numberPlate); // Reflect the updated plate in the TextView
                    } else {
                        // Handle failure
                        text.setText("Error updating slot");
                    }
                });
            }
        });
    }

    public void displayNumberPlate() {
        TextView txtNumber = findViewById(R.id.txtNumberPlate);
        DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Numberplate").child("image_10");

        dref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String numberPlate = dataSnapshot.getValue(String.class);
                txtNumber.setText(numberPlate != null && !numberPlate.equals("NULL") ? numberPlate : "CAC1212");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txtNumber.setText("Error loading data");
            }
        });
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
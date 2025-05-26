package com.example.drivein;

public class ParkingModel {

    private String plate;
    private String startTime;  // Changed from int to String
    private String duration;   // Changed from int to String
    private String status;

    // Required no-argument constructor for Firebase
    public ParkingModel() {
    }

    public ParkingModel(String plate, String startTime, String duration, String status) {
        this.plate = plate;
        this.startTime = startTime;
        this.duration = duration;
        this.status = status;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

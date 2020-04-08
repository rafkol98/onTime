package com.example.ontime;

public class User {

    double avgSpeed;
    String uID;

    public double getAvgSpeed() {
        return avgSpeed;
    }

    public User() {
    }

    public User( String uID,double avgSpeed) {
        this.avgSpeed = avgSpeed;
        this.uID = uID;
    }
}

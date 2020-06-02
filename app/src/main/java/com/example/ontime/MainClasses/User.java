package com.example.ontime.MainClasses;

/**
 * Store all the necessary information for a user.
 */

public class User {

    //Initialise variables.
    double avgSpeed;
    String uID;
    String currentLocation;

    //Getters.
    public double getAvgSpeed() {
        return avgSpeed;
    }

    public String getuID() {
        return uID;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    //Constructors.
    public User() {
    }

    public User( String uID,double avgSpeed) {
        this.avgSpeed = avgSpeed;
        this.uID = uID;
    }
}

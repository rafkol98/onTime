package com.example.ontime;

public class Trip {


    String destination;
    String date;
    String time;


    public Trip(String destination, String date, String time) {
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    public String getDestination() {
        return destination;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}

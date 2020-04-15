package com.example.ontime;

public class Trip {


    String destination;
    String date;
    String time;
    Object object;


    public Trip(String destination, String date, String time) {
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    public Trip(Object object) {
        this.object = object;
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

    //Get unique hash key of the trip
    public int getTripId(String destination, String date, String time){
        String date_time = (destination+date+time);

        int hash = 17;
        for (int i = 0; i < date_time.length(); i++) {
            hash = hash*31 + date_time.charAt(i);
        }
        return hash;
    }
}

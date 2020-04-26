package com.example.ontime;

public class Trip implements Comparable<Trip>{


    String destination;
    String date;
    String time;
    Long timestamp;
    Object object;


    public Trip(String destination, String date, String time) {
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    public Trip(String destination, Long timestamp) {
        this.destination = destination;
        this.timestamp = timestamp;
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

    public Long getTimestamp() {
        return timestamp;
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


    @Override
    public int compareTo(Trip o) {
        if ((Long) this.getTimestamp() < (Long) o.getTimestamp())
            return -1;
        if (this.getTimestamp().equals(o.getTimestamp()))
            return 0;

        return 1;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}

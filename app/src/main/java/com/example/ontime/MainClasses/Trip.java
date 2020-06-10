package com.example.ontime.MainClasses;

/**
 * Store specific information about a walk/trip.
 */
public class Trip implements Comparable<Trip>{

    //initialise variables. Timestamp is used because time is stored as a long in the firebase database.
    String destination;
    String date;
    String time;
    Long timestamp;
    Object object;

    /**
     *
     * @param destination
     * @param date
     * @param time
     */
    public Trip(String destination, String date, String time) {
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    /**
     *
     * @param destination
     * @param timestamp
     */
    public Trip(String destination, Long timestamp) {
        this.destination = destination;
        this.timestamp = timestamp;
    }

    /**
     *
     * @param object
     */
    public Trip(Object object) {
        this.object = object;
    }

    /**
     *
     * @return
     */
    public String getDestination() {
        return destination;
    }

    /**
     *
     * @return
     */
    public String getDate() {
        return date;
    }

    /**
     *
     * @return
     */
    public String getTime() {
        return time;
    }

    /**
     *
     * @return
     */
    public Long getTimestamp() {
        return timestamp;
    }

    /**
     * Get unique hash key of the trip. Used to generate a unique tripId for each trip.
     * @param destination
     * @param date
     * @param time
     * @return
     */
    public int getTripId(String destination, String date, String time){
        String date_time = (destination+date+time);

        int hash = 17;
        for (int i = 0; i < date_time.length(); i++) {
            hash = hash*31 + date_time.charAt(i);
        }
        return hash;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Trip o) {
        if ((Long) this.getTimestamp() < (Long) o.getTimestamp())
            return -1;
        if (this.getTimestamp().equals(o.getTimestamp()))
            return 0;

        return 1;
    }

    /**
     *
     * @param l
     * @return
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
}

package com.example.ontime.MainClasses;

/**
 * Store specific information about a walk/trip.
 */
public class Trip implements Comparable<Trip> {

    //initialise variables. Timestamp is used because time is stored as a long in the firebase database.
    int tripId;
    String destination;
    double latitude;
    double longitude;
    String date;
    String time;
    Long timestamp;
    Object object;
    double flagValue10;
    double flagValue1;
    boolean shouldAlert10 = true;
    boolean shouldAlert1 = true;

    boolean meetingFlag;

    /**
     * Constructor
     * @param destination - Destination of interest
     * @param date - Date of trip
     * @param time - Time of trip
     */
    public Trip(String destination, String date, String time) {
        this.destination = destination;
        this.date = date;
        this.time = time;
    }

    /**
     * Constructor
     * @param destination - Destination of interest
     * @param timestamp - Timestamp for when destination is planned
     * @param latitude - Latitude of location
     * @param longitude - Longitude of location
     */
    public Trip (String destination, Long timestamp, double latitude, double longitude) {
        this.destination = destination;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    /**
     * Constructor
     * @param destination - Destination of interest
     * @param timestamp - Timestamp for when destination is planned
     * @param latitude - Latitude of location
     * @param longitude - Longitude of location
     * @param meetingFlag - Is this a meeting?
     */
    public Trip (String destination, Long timestamp, double latitude, double longitude, boolean meetingFlag) {
        this.destination = destination;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.meetingFlag = meetingFlag;
    }

    /**
     * Constructor
     * @param destination - Destination of interest
     * @param timestamp - Timestamp for when trip is planned
     */
    public Trip(String destination, Long timestamp) {
        this.destination = destination;
        this.timestamp = timestamp;
    }


    /**
     * @param destination
     * @param timestamp
     */
    public Trip(String destination, Long timestamp, boolean meetingFlag) {
        this.destination = destination;
        this.timestamp = timestamp;
        this.meetingFlag = meetingFlag;
    }


    /**
     * Constructor
     * @param object ?
     */
    public Trip(Object object) {
        this.object = object;
    }

    /**
     * Getter for Destination
     * @return destination of trip
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Getter for Date
     * @return date of trip
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for Time
     * @return time of trip
     */
    public String getTime() {
        return time;
    }

    /**
     * Getter for Latitude
     * @return latitude of destination
     */
    public double getLatitude() { return latitude; }

    /**
     * Getter for Longitude
     * @return longitude of destination
     */
    public double getLongitude() { return longitude; }

    /**
     *
     * @return
     */
    public Long getTimestamp() {
        return timestamp;
    }


    /**
     * Setter for Distance from
     * @param flagValue10 the distance of the users current location from trip destination
     */
    public void setFlagValue10(double flagValue10) { this.flagValue10 = flagValue10; }

    /**
     * Getter for Distance From
     * @return the distance of the users current location from trip destination
     */
    public double getFlagValue10() { return flagValue10; }

    /**
     * Setter for Distance from
     * @param flagValue1 the distance of the users current location from trip destination
     */
    public void setFlagValue1(double flagValue1) { this.flagValue1 = flagValue1; }

    /**
     * Getter for Distance From
     * @return the distance of the users current location from trip destination
     */
    public double getFlagValue1() { return flagValue1; }

    public boolean getShouldAlert10() { return shouldAlert10; }

    public void setShouldAlert10 (boolean shouldAlert10) { this.shouldAlert10 = shouldAlert10; }

    public boolean getShouldAlert1() { return shouldAlert1; }

    public void setShouldAlert1 (boolean shouldAlert1) { this.shouldAlert1 = shouldAlert1; }

    /**
     * Get unique hash key of the trip. Used to generate a unique tripId for each trip.
     *
     * @param destination
     * @param date
     * @param time
     * @return
     */
    public int getTripId(String destination, String date, String time) {
        String date_time = (destination + date + time);

        int hash = 17;
        for (int i = 0; i < date_time.length(); i++) {
            hash = hash * 31 + date_time.charAt(i);
        }
        return hash;
    }

    /**
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
     * Convert Long to Integer
     * @param l long to be converted
     * @return integer value
     */
    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new IllegalArgumentException
                    (l + " cannot be cast to int without changing its value.");
        }
        return (int) l;
    }
  
    public boolean isMeetingFlag() {
        return meetingFlag;
    }
}

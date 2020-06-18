package com.example.ontime.SignIn_UpClasses;

import android.location.Location;

/**
 * Class that is used to update the speed of the user as he completed the test.
 */
public class CLocation extends Location {

    private boolean bUserMetricUnits = false;

    /**
     *
     * @param location
     */
    public CLocation(Location location){
        this(location,true);
    }

    /**
     *
     * @param l
     * @param bUserMetricUnits
     */
    public CLocation(Location l, boolean bUserMetricUnits) {
        super(l);
        this.bUserMetricUnits = bUserMetricUnits;
    }

    /**
     *
     * @return
     */
    public boolean getUseMetricUnits() {
        return bUserMetricUnits;
    }

    /**
     *
     * @param bUserMetricUnits
     */
    public void setbUserMetricUnits(boolean bUserMetricUnits) {
        this.bUserMetricUnits = bUserMetricUnits;
    }

    /**
     *
     * @param dest
     * @return
     */
    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);

        if(!this.getUseMetricUnits()) {
        //Convert meters to feet
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

    /**
     *
     * @return
     */
    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();

        if(!this.getUseMetricUnits()) {
            //Convert meters to feet
            nAltitude = nAltitude * 3.28083989501312f;
        }
        return nAltitude;

    }

    /**
     *
     * @return
     */
    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;

        if(!this.getUseMetricUnits()) {
            //Convert meters/second to miles/hour
            nSpeed = nSpeed * 2.23693629f;
        }
        return nSpeed;
    }

    /**
     *
     * @return
     */
    @Override
    public float getAccuracy() {
        float nAccuracy = super.getAccuracy();

        if(!this.getUseMetricUnits()) {
            //Convert meters to feet
            nAccuracy = nAccuracy * 3.28083989501312f;
        }
        return nAccuracy;
    }
}

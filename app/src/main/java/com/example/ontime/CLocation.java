package com.example.ontime;

import android.location.Location;

public class CLocation extends Location {

    private boolean bUserMetricUnits = false;

    public CLocation(Location location){
        this(location,true);
    }

    public CLocation(Location l, boolean bUserMetricUnits) {
        super(l);
        this.bUserMetricUnits = bUserMetricUnits;
    }

    public boolean getUseMetricUnits() {
        return bUserMetricUnits;
    }

    public void setbUserMetricUnits(boolean bUserMetricUnits) {
        this.bUserMetricUnits = bUserMetricUnits;
    }

    @Override
    public float distanceTo(Location dest) {
        float nDistance = super.distanceTo(dest);

        if(!this.getUseMetricUnits()) {
        //Convert meters to feet
            nDistance = nDistance * 3.28083989501312f;
        }
        return nDistance;
    }

    @Override
    public double getAltitude() {
        double nAltitude = super.getAltitude();

        if(!this.getUseMetricUnits()) {
            //Convert meters to feet
            nAltitude = nAltitude * 3.28083989501312f;
        }
        return nAltitude;

    }

    @Override
    public float getSpeed() {
        float nSpeed = super.getSpeed() * 3.6f;

        if(!this.getUseMetricUnits()) {
            //Convert meters/second to miles/hour
            nSpeed = nSpeed * 2.23693629f;
        }
        return nSpeed;
    }

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

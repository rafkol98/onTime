package com.example.ontime.RestarterAndServices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.example.ontime.R;
import com.example.ontime.utilities.Notification;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 */
public class Service extends android.app.Service {
    private static final String CHANNEL_ID = "ServiceChannel";
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private int counter = 0;
    private ArrayList<Trip> trips;

    //get firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    /**
     *
     */
    public Service() {
        super();
    }

    /**
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "restarting Service !!");
        counter = 0;

        // it has been killed by Android and now it is restarted. We must make sure to have reinitialised everything
        if (intent == null) {
            ProcessClass bck = new ProcessClass();
            bck.launchService(this);
        }

        startForeground(1, Notification.setNotification(this,
                "Location Services Running",
                "To ensure accuracy please do not cancel.",
                R.drawable.ic_notification));

        trips = new ArrayList<>();
        getTrips();
        startTimer();
        startLocationService();

        // return START_STICKY so if it is killed by android, it will be restarted with Intent null
        return START_STICKY;
    }

    /**
     *
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        mCurrentService = this;
    }

    /**
     *
     * @param intent
     * @return null
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     *
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Constants.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        stopLocationService();

    }

    /**
     * It starts the process in foreground. Normally this is done when screen goes off
     */
    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "restarting foreground");
            try {
                startForeground(1, Notification.setNotification(this,
                        "Location Services Running",
                        "To ensure accuracy please do not cancel.",
                        R.drawable.ic_notification));
                startTimer();
                startLocationService();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.e(TAG, "Error in notification " + e.getMessage());
            }
        }
    }

    /**
     * This is called when the process is killed by Android
     * @param rootIntent
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG, "onTaskRemoved called");
        // restart the never ending service
        Intent broadcastIntent = new Intent(Constants.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }


    //static to avoid multiple timers to be created when the service is called several times
    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    /**
     * Method that starts the timer.
     */
    public void startTimer() {
        Log.i(TAG, "Starting timer");

        //set a new Timer - if one is already running, cancel it to avoid two running at the same time
        stoptimertask();
        stopLocationService();
        timer = new Timer();

        //initialize the TimerTask's job
        LogTimerTask();

        //I also tried to start the location service. Which gets the user's location every 4 seconds.
        // This works, but once the app is restarted or goes in to background, it stops working after a few seconds.
        startLocationService();
        Log.i(TAG, "Scheduling...");
        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000);
    }

    private LocationCallback locationCallback = new LocationCallback() {
        /**
         *
         * @param locationResult
         */
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                //write current location on the database.
                final String uId = currentFirebaseUser.getUid();

                DatabaseReference childReff = dbRef.child(uId).child("Current Location");

                String cLocation = latitude + "," + longitude;
                childReff.setValue(cLocation, new DatabaseReference.CompletionListener() {
                    /**
                     * Upon completion of setValue, log the completion results in FirebaseCrashlytics
                     * @param databaseError containing details of error if one occurred
                     * @param databaseReference reference of database
                     */
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                            FirebaseCrashlytics.getInstance().log(databaseError.getDetails());
                        } else {
                            FirebaseCrashlytics.getInstance().log("Successful write of Location");
                        }
                    }
                });

                calculateDistanceFromTripDestination(latitude, longitude);

                determineTimeFromTripDestination();

                shouldAlert();

                Log.d("LOCATION_UPDATE", latitude + "," + longitude);
            }
        }
    };

    private void shouldAlert() {
        for (Trip trip : trips) {
            if (trip.getShouldAlert()){
                // ALERT THE USER ABOUT THE TRIP
            }
        }
    }

    private void determineTimeFromTripDestination() {
        String uId = currentFirebaseUser.getUid();
        double speed = dbRef.child(uId).child("Average Speed")
        for (Trip trip : trips) {

        }
    }

    /**
     * Calculate the distance from current location to the destination location in kilometers
     * @param latitude of the users current location
     * @param longitude of the users current location
     */
    private void calculateDistanceFromTripDestination(double latitude, double longitude) {
        latitude = Math.toRadians(latitude);
        longitude = Math.toRadians(longitude);

        for (Trip trip : trips) {
            if (trip.getLatitude() != 0.0 || trip.getLongitude() != 0.0){
                double destLat = Math.toRadians(trip.getLatitude());
                double destLng = Math.toRadians(trip.getLongitude());

                double delta_lng = destLng - longitude;
                double delta_lat = destLat - latitude;
                double a = Math.pow(Math.sin(delta_lat / 2), 2)
                        + Math.cos(latitude) * Math.cos(destLat)
                        * Math.pow(Math.sin(delta_lng / 2), 2);

                double c = 2 * Math.asin(Math.sqrt(a));

                // Radius in kilometers
                double r = 6371;
                // Radius in miles
               // double r = 3956;
                trip.setDistanceFrom(r * c);
            }
        }
    }

    /**
     * Start the service, get location of the user every 4 seconds.
     */
    private void startLocationService() {

        Log.d(TAG, "Trying to start location......");
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(this).
                requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }


    /**
     * Stop the Location service.
     */
    private void stopLocationService() {
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
//        stopForeground(true);
//        stopSelf();
    }

    /**
     * Log the counter every 1 second.
     */
    public void LogTimerTask() {
        Log.i(TAG, "initialising TimerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));
            }
        };
    }

    /**
     * Stop the timer.
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void getTrips(){
        String uId = currentFirebaseUser.getUid();
        //Get trips of the user. Order them so that the closest one to the current date is first.
        dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    String destination = "";
                    Long timestamp;
                    double latitude = 0.0;
                    double longitude = 0.0;
                    Trip trip;
                    try{
                        destination = child.child("destination").getValue().toString();
                        latitude = child.child("latitude").getValue(Double.class);
                        longitude = child.child("longitude").getValue(Double.class);
                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    timestamp = child.child("timestamp").getValue(Long.class);

                    if (latitude != 0.0 && longitude != 0.0){
                        trip = new Trip(destination, timestamp, latitude, longitude);
                    } else {
                        trip = new Trip(destination, timestamp);
                    }
                    trips.add(trip);
                }

                Collections.sort(trips);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
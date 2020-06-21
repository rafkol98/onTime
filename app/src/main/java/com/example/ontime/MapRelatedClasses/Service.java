package com.example.ontime.MapRelatedClasses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.example.ontime.R;
import com.example.ontime.RestarterAndServices.Constants;
import com.example.ontime.RestarterAndServices.ProcessClass;
import com.example.ontime.utilities.Notification;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
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

    private final String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};

    static final int PERMISSION_ALL = 134;

    private static double avgSpeed;
    private double timeToDest;

    int valueFlag;

    //get firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    //write current location on the database.
    final String uId = currentFirebaseUser.getUid();


    /**
     *
     */
    public Service() {
        super();
    }

    /**
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
     *
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

                updateTrips();

                Log.d("LOCATION_UPDATE", latitude + "," + longitude);
            }
        }
    };

    private void updateTrips() {

    }

    /**
     * Calculate the distance from current location to the destination location in kilometers
     *
     * @param latitude  of the users current location
     * @param longitude of the users current location
     */
    private void calculateDistanceFromTripDestination(double latitude, double longitude) {
        float[] results = new float[1];

        if (avgSpeed == 0) {
            String uId = currentFirebaseUser.getUid();
            dbRef.child(uId).child("Average Speed").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        avgSpeed = Double.valueOf(dataSnapshot.getValue().toString());
                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                    FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                }
            });
        }

        // Skip if failed to obtain the users speed
        if (avgSpeed == 0) return;

        for (Trip trip : trips) {

                //call the GeoService for every trip. The GeoService calculates in its calculateTimeAndDist method how much time the user needs to walk there from his current location.
                GeoService geoService = new GeoService(trip);
                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + latitude + "," + longitude + "&destinations=" + trip.getLatitude() + "," + trip.getLongitude() + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";
                Log.d("url string", url);
                geoService.execute(url);

        }
    }


    /**
     * Start the service, get location of the user every 4 seconds.
     */
    @SuppressLint("MissingPermission")
    private void startLocationService() {

        Log.d(TAG, "Trying to start location......");
        LocationRequest locationRequest = new LocationRequest();

        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).
                    requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } else {
            //ActivityCompat.requestPermissions(, PERMISSIONS, PERMISSION_ALL);
        }
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

    private void getTrips() {
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
                    try {
                        destination = child.child("destination").getValue().toString();
                        latitude = child.child("latitude").getValue(Double.class);
                        longitude = child.child("longitude").getValue(Double.class);

                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    timestamp = child.child("timestamp").getValue(Long.class);

                    if (latitude != 0.0 && longitude != 0.0) {
                        trip = new Trip(destination, timestamp, latitude, longitude);
                    } else {
                        trip = new Trip(destination, timestamp);
                    }
                    trips.add(trip);
                }
                try {
                    Collections.sort(trips);
                } catch (Exception e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /**
     * Inner class to calculate with distance matrix api the time needed to walk to each location.
     */
    class GeoService extends AsyncTask<String, Void, String> {
        ProgressDialog pd;
        Context mContext;

        Trip trip;

        /**
         * Constructor is used to get the context.
         *
         * @param mContext
         */
        public GeoService(Context mContext) {
            this.mContext = mContext;
        }

        public GeoService(Trip trip) {
            this.trip = trip;
        }

        //

        /**
         * This function is executed after the execution of "doInBackground(String...params)" it calculates whetet to notify the user to start walking or not.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                double timeToDest = calculateTimeAndDist(result);

                //Read flag's value
                dbRef.child(uId).child("trips").child((trip.getTimestamp()).toString()).child("flagValue").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            valueFlag = Integer.valueOf(dataSnapshot.getValue().toString());
                            Log.d("valueFlag value: ", valueFlag + "");
                        } catch (NullPointerException e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        System.out.println("The read failed: " + databaseError.getCode());
                        FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                    }
                });

//                 Instantiate a new calendar object
                    Calendar calendar = Calendar.getInstance();

                    // Add the time it would take to reach in milliseconds to the users current time
                    calendar.add(Calendar.MINUTE, (int) timeToDest);

                    // Get the time of the of when they would arrive in milliseconds
                    long time = calendar.getTimeInMillis();

                    Log.d("time arrival start now", time + "");

                System.out.println(" ");

                //if the difference is less than 10 minutes notify user.
                boolean shouldAlert = (trip.getTimestamp() - time) < 600000;

                if (shouldAlert && valueFlag==0) {
                    if (trip.getShouldAlert()) {
                        int id = trip.getTripId(trip.getDestination(), trip.getDate(), trip.getTime());

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Notification.showNotification(getApplicationContext(),
                                    "Should start walking",
                                    "In 10 minutes start walking to " + trip.getDestination() + " in order to arrive on time.",
                                    R.drawable.ic_notification, id, NotificationManager.IMPORTANCE_MAX);


                            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 2000 milliseconds
                            v.vibrate(2000);


                        } else {
                            Notification.showNotification(getApplicationContext(),
                                    "Should start walking",
                                    "In 10 minutes start walking to " + trip.getDestination() + " in order to arrive on time.",
                                    R.drawable.ic_notification, id);

                            Vibrator v = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
                            // Vibrate for 2000 milliseconds
                            v.vibrate(2000);
                        }
                        trip.setShouldAlert(false);

                        //write current location on the database.
                        final String uId = currentFirebaseUser.getUid();

                        DatabaseReference flagReff = dbRef.child(uId).child("trips").child((trip.getTimestamp()).toString()).child("flagValue");

                        int newValueFlag = 1;

                        flagReff.setValue(newValueFlag, new DatabaseReference.CompletionListener() {
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

                    }
                }

            }
        }


        /**
         * Gets duration and distance in the background. Remember, this is an AsyncTask so it doesn't
         * run with the natural flow of the program. It runs in the background.
         *
         * @param params
         * @return
         */
        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.connect();
                int statuscode = con.getResponseCode();
                if (statuscode == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line = br.readLine();
                    while (line != null) {
                        sb.append(line);
                        line = br.readLine();
                    }
                    String json = sb.toString();
                    JSONObject root = new JSONObject(json);
                    JSONArray array_rows = root.getJSONArray("rows");
                    JSONObject object_rows = array_rows.getJSONObject(0);
                    JSONArray array_elements = object_rows.getJSONArray("elements");
                    JSONObject object_elements = array_elements.getJSONObject(0);
                    JSONObject object_duration = object_elements.getJSONObject("duration");
                    JSONObject object_distance = object_elements.getJSONObject("distance");


                    return object_duration.getString("value") + "," + object_distance.getString("value");

                }
            } catch (MalformedURLException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("error", "error1");
            } catch (IOException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("error", "error2");
            } catch (JSONException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("error", "error3");
            }


            return null;
        }


        //Use distance matrix api to calculate how much time the user actually needs to go there.
        public double calculateTimeAndDist(String result) {
            String[] res = result.split(",");
            Double min = Double.parseDouble(res[0]) / 60;
            Double dist = Double.parseDouble(res[1]) / 1000;

            Double d = Double.valueOf(dist);
            Double s = Double.valueOf(avgSpeed);

            timeToDest = ((d / s) * 60);


            Log.d("time to dest "+trip.getDestination(), timeToDest + "");
            return timeToDest;

        }

    }
}

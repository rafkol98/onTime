package com.example.ontime;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service {

//
//    //get firebase user.
//    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
//
//
//    private LocationCallback locationCallback = new LocationCallback() {
//        @Override
//        public void onLocationResult(LocationResult locationResult) {
//            super.onLocationResult(locationResult);
//            if (locationResult != null && locationResult.getLastLocation() != null) {
//                double latitude = locationResult.getLastLocation().getLatitude();
//                double longitude = locationResult.getLastLocation().getLongitude();
//
//                //write current location on the database.
//                final String uId = currentFirebaseUser.getUid();
//
//                DatabaseReference childReff = dbRef.child(uId).child("Current Location");
//
//                String cLocation = latitude + "," + longitude;
//                childReff.setValue(cLocation);
//
//
//                Log.d("LOCATION_UPDATE", latitude + "," + longitude);
//            }
//        }
//
//
//    };
//
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
//
//    // initialise broadcast that will restart the service if app is closed.
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("restartservice");
//        broadcastIntent.setClass(this, Restarter.class);
//        this.sendBroadcast(broadcastIntent);
//    }
//
//    //Start the service
//    private void startLocationService() {
//        String channelId = "location_modification_channel";
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        Intent resultIntent = new Intent();
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //Notifications
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("Location service");
//        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
//        builder.setContentText("Running");
//        builder.setContentIntent(pendingIntent);
//        builder.setAutoCancel(false);
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (notificationManager != null && notificationManager.getNotificationChannel(channelId) == null) {
//                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
//                notificationChannel.setDescription("This channel is used by location service");
//                notificationManager.createNotificationChannel(notificationChannel);
//            }
//        }
//
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(4000);
//        locationRequest.setFastestInterval(2000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());
//
//    }
//
//
//    // Stop the service
//    private void stopLocationService() {
//        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
//        stopForeground(true);
//        stopSelf();
//    }
//
//
//    //Initilaise startLocationService
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent != null) {
//            String action = intent.getAction();
//            if (action != null) {
//                if (action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
//                    startLocationService();
//                }
//
////                else if (action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)){
////                    stopLocationService();
////                }
//            }
//        }
//        return START_STICKY;
//    }






    public int counter=0;
    public LocationService(Context applicationContext) {
        super();
        Log.i("HERE", "here I am!");
    }

    public LocationService() {
    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        startForeground(1,new Notification());
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("EXIT", "ondestroy!");
        Intent broadcastIntent = new Intent(this, Restarter.class);

        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}



package com.example.ontime.SignIn_UpClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ontime.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Formatter;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This class is used to calculate the user's average speed, It has a countdown of 2 minutes.
 */
public class Countdown extends AppCompatActivity implements LocationListener {

    //Initialise variables.
    SwitchCompat sw_metric;
    TextView txt, avgTxt;


    private int seconds = 59;
    private int minutes = 1;
    private TextView timer;
    double totalSpeed;
    double avgTSpeed;
    boolean b = false;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_countdown);


        sw_metric = findViewById(R.id.sw_speed);
        txt = findViewById(R.id.speedometer_txt);
        avgTxt = findViewById(R.id.avgSpeedTxt);


        //Used to check for gps permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1000);
        } else {
            //start the program if the permission is granted
            doStuff();
        }

        this.updateSpeed(null);

        sw_metric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            /**
             *
             * @param buttonView
             * @param isChecked
             */
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Countdown.this.updateSpeed(null);
            }
        });


        //Declare the timer
        Timer t = new Timer();
        //Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        timer = (TextView) findViewById(R.id.timer);
                        timer.setText("0" + String.valueOf(minutes) + ":" + String.valueOf(seconds));

                        if (minutes == 0 && seconds == 0) {
                            timer.setText("00:00");

                        }
                        if (seconds > 0) {
                            seconds -= 1;
                        }
                        if (seconds < 10) {
                            timer.setText("0" + String.valueOf(minutes) + ":0" + String.valueOf(seconds));
                        }
                        if (seconds == 0 && minutes == 1) {
                            timer.setText("0" + String.valueOf(minutes) + ":00");

                            seconds = 59;
                            minutes = minutes - 1;
                        }


                    }
                });
            }
        }, 0, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                avgTSpeed = totalSpeed / 119;
                // Write user to the database
                String uId = currentFirebaseUser.getUid();

                //Make double into string
                String avg = String.valueOf(avgTSpeed);

                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(avgTSpeed);

                Intent i = new Intent(Countdown.this, Cool.class);
                startActivity(i);
                finish();
            }
        }, 119000);

//          ^  change it to 61000


    }

    /**
     *
     */
    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        }
        Toast.makeText(this, "Waiting for GPS connection!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Update speed of the user.
     * @param location
     */
    private void updateSpeed(CLocation location) {
        double nCurrentSpeed = 0;
        if (location != null) {
            location.setbUserMetricUnits(this.useMetricUnits());
            nCurrentSpeed = location.getSpeed();
            totalSpeed = totalSpeed + nCurrentSpeed;
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");

        if (this.useMetricUnits()) {
            txt.setText(strCurrentSpeed + " km/h");
            avgTxt.setText("total " + totalSpeed + " km/h");
        } else {
            txt.setText(strCurrentSpeed + " miles/h");
            avgTxt.setText("total " + totalSpeed + " miles/h");
        }
    }

    /**
     * Grant permission.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
        }
    }

    /**
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            CLocation myLocation = new CLocation(location, this.useMetricUnits());
            this.updateSpeed(myLocation);
        }
    }

    /**
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) { }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) { }

    /**
     *
     * @return
     */
    private boolean useMetricUnits() { return sw_metric.isChecked(); }

    /**
     *
     */
    public void onBackPressed() { }

}









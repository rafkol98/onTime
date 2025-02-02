package com.example.ontime.DateTimeClasses;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MapRelatedClasses.Map;
import com.example.ontime.R;
import com.example.ontime.MainClasses.SuperScreen;
import com.example.ontime.MainClasses.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This class is used to enable the user to select the date and time of the trip.
 */
public class SelectTime extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
                                                            TimePickerFragment.TimePickerListener {

    /*
    Declare variables.
    */

    //DestinationPassed is the destination that the user selected for the trip. The destinationPassed is passed from the Map class.
    String destinationPassed, datePassed, timePassed, stringIn;

    private TextView dateText;
    private TextView timeText;


    double desLat, desLng;

    Trip trip;
    Map map;
    double time, tt;
    DateTimeCheck dateTimeCheck;

    /**
     *
     * @return time
     */
    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);
        map = new Map();
        dateTimeCheck = new DateTimeCheck();

        //assign the text fields of date and time.
        dateText = findViewById(R.id.selectDate_txt);
        timeText = findViewById(R.id.timer_txt);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("keyMap");
            tt = extras.getDouble("keyTimeToDest");
            stringIn = extras.getString("keyTime");
            desLat = extras.getDouble("keyLatitude");
            desLng = extras.getDouble("keyLongitude");

        }


        findViewById(R.id.selectDate_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        findViewById(R.id.timer_txt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePickerFragment = new TimePickerFragment();
                timePickerFragment.setStyle(DialogFragment.STYLE_NORMAL,R.style.MyTimePickerDialogTheme);
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

    }

    /**
     * Shows the date picker dialog.
     */
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.MyDatePickerDialogTheme, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    /**
     * onDateSet set it sets the textview to the date selected.
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dateText.setText(date);
    }

    /**
     * onTimeSet set it sets the TextView to the time selected.
     * @param timePicker - time picker widget for selecting time
     * @param hour - hour
     * @param minute - minute
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        timeText.setText(String.format("%02d:%02d", hour, minute));
    }


    /**
     * When the user clicks the done button, this method is called.
     * @param 
     * @throws ParseException
     */
    public void onDone(View v) throws ParseException {
        String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString() + ":00";

        double timeToWalk = 0;

        //Get current date.
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(date);
        //Get the time to walk there based on the user's speed.
        if(stringIn!=null) {
            try {
                timeToWalk = Double.parseDouble(stringIn);
            } catch (Exception e){
                e.printStackTrace();
            }

        }
        //temp is time to walk to destination.
        int temp = (int) timeToWalk;

        //Get time in difference between current date and trip's desired date.
        int minutesDate = DateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), strDate, dateSelected);



        if ((dateText.getText().toString()).equalsIgnoreCase("date") && (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        }
        if ((dateText.getText().toString()).equalsIgnoreCase("date") || (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        }
        //Check if time walking is more than the difference in minutes between current time and desired arrival time.
        else if (minutesDate < temp && minutesDate >= 1) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You cant make it there on time walking, you would have to speed up, you need " + timeToWalk + "minutes to go there and you have to be there in " + minutesDate + " minutes. Do you want to proceed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            try {
                                doTrip();
                            } catch (ParseException e) {
                                FirebaseCrashlytics.getInstance().recordException(e);
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(SelectTime.this, MPage.class));
                            overridePendingTransition(0,0);
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else if (minutesDate < temp && minutesDate < 1) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You cant make it there on time please select another time")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.dismiss();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();


        } else {
            //Call doTrip method to upload trip to firebase.
            try {
                doTrip();
            } catch (ParseException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }

        }


    }

    /**
     * Uploads trip to firebase for the user.
     * @throws ParseException
     */
    public void doTrip() throws ParseException {

        //Store date,time and destination on a new Trip.
        datePassed = dateText.getText().toString();
        timePassed = timeText.getText().toString();

        String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString();
        Long timestamp = toMilli(dateSelected);

        trip = new Trip(destinationPassed, timestamp, desLat, desLng, false);

        trip.setShouldAlert10(false);
        trip.setShouldAlert1(false);


        //Get uId of the Firebase User.
        String uId = currentFirebaseUser.getUid();
        //Create a unique Hash Key for the Trip.
        String tripId = Long.toString(trip.getTimestamp());

        //Store the trip on Firebase RealTime Database.
        DatabaseReference childReff = dbRef.child(uId).child("trips").child(tripId);
        childReff.setValue(trip, new DatabaseReference.CompletionListener() {
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


        Intent myIntent = new Intent(SelectTime.this, SuperScreen.class);
        myIntent.putExtra("keyDest", trip.getDestination());
        startActivity(myIntent);
        overridePendingTransition(0,0);

    }

    /**
     * Converts date to milliseconds.
     * @param dateIn - Date to convert to milliseconds (ms)
     * @return date in milliseconds (ms)
     * @throws ParseException - Signals that an error has been reached unexpectedly while parsing.
     */
    public Long toMilli(String dateIn) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = (Date) formatter.parse(dateIn);
        long output = 0;

        try{
            output = date.getTime() / 1000L;
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return output * 1000;
    }


}




package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SelectTime extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerFragment.TimePickerListener {

    String destinationPassed, datePassed, timePassed, stringIn;

    private TextView dateText;
    private TextView timeText;

    Trip trip;
    Map map;
    double time, tt;
    DateTimeCheck dateTimeCheck;


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
//        geoTask= new Map.GeoTask();

        dateText = findViewById(R.id.selectDate_txt);
        timeText = findViewById(R.id.timer_txt);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("keyMap");
            tt = extras.getDouble("keyTimeToDest");
            stringIn = extras.getString("keyTime");
            Log.d("HERE HERE MALAKA", stringIn);

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
                timePickerFragment.setCancelable(false);
                timePickerFragment.show(getSupportFragmentManager(), "timePicker");
            }
        });

    }


    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.MyDatePickerDialogTheme, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dateText.setText(date);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        timeText.setText(hour + ":" + minute);
    }

    public void onDone(View v) throws ParseException {
        String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString() + ":00";
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(date);
        //Get the time to walk there based on the user's speed.
        double timeToWalk = Double.parseDouble(stringIn);
        int temp = (int) timeToWalk;


        int minutesDate = dateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), strDate, dateSelected);


        if ((dateText.getText().toString()).equalsIgnoreCase("date") && (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        }
        if ((dateText.getText().toString()).equalsIgnoreCase("date") || (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        }
        //Check if time walking is more than the difference in minutes between current time and desired arrival time.
        else if (minutesDate < temp) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You cant make it there on time walking, you would have to speed up, you need " + timeToWalk + "minutes to go there and you have to be there in " + minutesDate + ". Do you want to proceed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            try {
                                doTrip();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(SelectTime.this, Menu.class));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            //Call doTrip method to upload trip to firebase.
            try {
                doTrip();
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }


    }

    //uploads trip to firebase for the user.
    public void doTrip() throws ParseException {

        //Store date,time and destination on a new Trip.
        datePassed = dateText.getText().toString();
        timePassed = timeText.getText().toString();

        String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString();
        Long timestamp = toMilli(dateSelected);

//        trip = new Trip(destinationPassed, datePassed, timePassed);
        trip = new Trip(destinationPassed,timestamp);
        //Get uId of the Firebase User.
        String uId = currentFirebaseUser.getUid();
        //Create a unique Hash Key for the Trip.
        String tripId = Integer.toString(trip.getTripId(destinationPassed, datePassed, timePassed));

        //Store the trip on Firebase RealTime Database.
        DatabaseReference childReff = dbRef.child(uId).child("trips").child(tripId);
        childReff.setValue(trip);


        Intent myIntent = new Intent(SelectTime.this, SuperScreen.class);
        myIntent.putExtra("keyDest", trip.getDestination());
        startActivity(myIntent);

    }

    public Long toMilli(String dateIn) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = (Date) formatter.parse(dateIn);
        long output = date.getTime() / 1000L;
        String str = Long.toString(output);
        long timestamp = Long.parseLong(str) * 1000;
        return timestamp;
    }


}




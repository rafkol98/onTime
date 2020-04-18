package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SelectTime extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerFragment.TimePickerListener {

    String destinationPassed, datePassed, timePassed, boobs;

    private TextView dateText;
    private TextView timeText;
    private TextView minTodest;
    Trip trip;
    Map map;
    double time,tt;
    DateTimeCheck dateTimeCheck;
//    Map.GeoTask geoTask;
//    GeoTask gt;


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
            boobs = extras.getString("keyBoobs");
            Log.d("HERE HERE MALAKA",boobs);

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

    public void onDone(View v) {
        String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString() + ":00";
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String strDate = dateFormat.format(date);
        System.out.println("CHECK ITS HERE"+map.getMinutes());



        Log.d("HERE HERE datetest", "Test: " + dateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"), strDate, dateSelected));


        System.out.println("gggg"+map.getMinutes()+"ffff"+map.getDistance());




//        if(dateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"),x,dateSelected)>time){
//
//        }

        if ((dateText.getText().toString()).equalsIgnoreCase("date") && (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        }
        if ((dateText.getText().toString()).equalsIgnoreCase("date") || (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(SelectTime.this, "Please select both date and time!", Toast.LENGTH_LONG).show();
        } else {
            datePassed = dateText.getText().toString();

            timePassed = timeText.getText().toString();

            trip = new Trip(destinationPassed, datePassed, timePassed);

            String uId = currentFirebaseUser.getUid();
            String tripId = Integer.toString(trip.getTripId(destinationPassed, datePassed, timePassed));


            DatabaseReference childReff = dbRef.child(uId).child("trips").child(tripId);

            childReff.setValue(trip);


            Intent myIntent = new Intent(SelectTime.this, SuperScreen.class);
            myIntent.putExtra("keyDest", trip.getDestination());
            startActivity(myIntent);

        }


    }


}




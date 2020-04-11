package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class SelectTime extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerFragment.TimePickerListener {

    String destinationPassed;

    private TextView dateText;
    private TextView timeText;
    boolean flag;
    Trip trip;

    String datePassed, timePassed;


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    private DatabaseReference dbRef= FirebaseDatabase.getInstance().getReference("/profiles");




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_time);

        dateText = findViewById(R.id.selectDate_txt);
        timeText = findViewById(R.id.timer_txt);



        flag=false;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("keyMap");
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
        setFlag(true);
        datePassed = dateText.getText().toString();
        timePassed =timeText.getText().toString();

        trip = new Trip(destinationPassed,datePassed,timePassed);

        String uId = currentFirebaseUser.getUid();

        DatabaseReference childReff = dbRef.child(uId).child("trips").child("destination");
        DatabaseReference childReff1 = dbRef.child(uId).child("trips").child("date");
        DatabaseReference childReff2 = dbRef.child(uId).child("trips").child("time");

//                DatabaseReference childReff = reff.child("Users").child(uId);
        childReff.setValue(destinationPassed);
        childReff1.setValue(datePassed);
        childReff2.setValue(timePassed);

        Intent myIntent = new Intent(SelectTime.this, SuperScreen.class);
        myIntent.putExtra("keyDate", dateText.getText().toString());
        myIntent.putExtra("keyTime", timeText.getText().toString());
        myIntent.putExtra("keyMap", destinationPassed);
        startActivity(myIntent);


    }


    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

}




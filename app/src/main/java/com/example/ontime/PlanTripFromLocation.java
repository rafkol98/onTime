package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class PlanTripFromLocation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip_from_location);
    }

    public void onBackPressed(){
        Intent myIntent = new Intent(PlanTripFromLocation.this, Menu.class);
        startActivity(myIntent);
    }
}

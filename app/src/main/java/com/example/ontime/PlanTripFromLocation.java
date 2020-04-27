package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.AutoCompleteTextView;

public class PlanTripFromLocation extends AppCompatActivity {

    private AutoCompleteTextView destination;
    String destinationPassed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_trip_from_location);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("keyMap");
        }

        AutoCompleteTextView fromAutoCompleteTextView = findViewById(R.id.fromAutoComplete);
        fromAutoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(PlanTripFromLocation.this, android.R.layout.simple_list_item_1));

        AutoCompleteTextView toAutoCompleteTextView = findViewById(R.id.toAutoComplete);
        toAutoCompleteTextView.setText(destinationPassed);
        toAutoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(PlanTripFromLocation.this, android.R.layout.simple_list_item_1));
    }

    public void onBackPressed(){
        Intent myIntent = new Intent(PlanTripFromLocation.this, MPage.class);
        startActivity(myIntent);
    }
}

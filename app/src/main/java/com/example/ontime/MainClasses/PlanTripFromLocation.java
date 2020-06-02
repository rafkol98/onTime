package com.example.ontime.MainClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import com.example.ontime.AutoSuggestClasses.PlaceAutoSuggestAdapter;
import com.example.ontime.R;

/**
 * Plan trip from another location. This is used when the location of the target's destination is too far away from the current location of the user.
 */
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

    //Override onBackPressed. If the user clicks the back button he is transfered to MPage(=> Fragment 1).
    public void onBackPressed(){
        Intent myIntent = new Intent(PlanTripFromLocation.this, MPage.class);
        startActivity(myIntent);
    }
}

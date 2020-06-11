package com.example.ontime.MainClasses.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ontime.AutoSuggestClasses.*;
import com.example.ontime.MapRelatedClasses.*;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This is the main fragment of the app. The user can find and select a place to plan a walk to.
 */
public class Tab1 extends Fragment {

    //Initialise variables.
    private AutoCompleteTextView destination;
    private AutoCompleteTextView autoCompleteTextView;

    Button goBtn;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    /**
     * Required empty public constructor
     */
    public Tab1() { }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_tab1, container, false);

        //initialise buttons and variables used.
        goBtn = v.findViewById(R.id.go_btn);
        destination = v.findViewById(R.id.autoComplete1);

        //Autocomplete when the user searches for a location.
        autoCompleteTextView = v.findViewById(R.id.autoComplete1);

        return v;

    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

        //Go to map page. When the user clicks on the search button.
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Map.class);

                //pass the destination to the Map class. This will be used to find the exact place on the map.
                String destinationStr = destination.getText().toString();
                myIntent.putExtra("key", destinationStr);
                startActivity(myIntent);
            }
        });


    }




}
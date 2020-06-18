package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.ontime.AutoSuggestClasses.PlaceAutoSuggestAdapter;
import com.example.ontime.MapRelatedClasses.Map;


public class Plan_Meeting extends Fragment {


    Button meetBtn;
    //Initialise variables.
    private AutoCompleteTextView destination;
    private AutoCompleteTextView autoCompleteTextView;

    String destinationConfirmed;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

        //Go to map page. When the user clicks on the search button.
        meetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //pass the destination to the Map class. This will be used to find the exact place on the map.
                String destinationStr = destination.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("keyMeeting",destinationStr); // Put anything what you want

                Fragment fragmentMap = new Map_Meet();
                fragmentMap.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, fragmentMap);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();


            }
        });


        Bundle bundle = this.getArguments();
        //If the user confirmed the meeting (by clicking confirm on the map), then set the text of the AutocompleteTextView
        //as the destination.
        if (bundle != null) {
            destinationConfirmed = getArguments().getString("confirmedMeeting");
        }

        destination.setText(destinationConfirmed);



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan_meeting, container, false);

        //initialise buttons and variables used.
        meetBtn = v.findViewById(R.id.meet_btn);
        destination = v.findViewById(R.id.meetingAutoComplete);
        autoCompleteTextView = v.findViewById(R.id.meetingAutoComplete);

        return v;
    }
}
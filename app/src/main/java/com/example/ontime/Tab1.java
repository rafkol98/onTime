package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab1 extends Fragment {

    private AutoCompleteTextView destination;
    Button btnCounter,buttonTrips,goBtn;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    public Tab1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab1, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();



        //initialise buttons and variables used.
        btnCounter = v.findViewById(R.id.button_counter);
        buttonTrips = v.findViewById(R.id.buttonTrips);
        goBtn = v.findViewById(R.id.go_btn);
        destination = v.findViewById(R.id.autoComplete1);

//        Drawable btn_bg_green = findViewById(R.drawable.circle_button_green);
        AutoCompleteTextView autoCompleteTextView = v.findViewById(R.id.autoComplete1);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

        final String uId = currentFirebaseUser.getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long childrenL = dataSnapshot.child(uId).child("trips").getChildrenCount();
                String counter = Long.toString(childrenL);
                if (childrenL != 0){
                    btnCounter.setBackgroundResource(R.drawable.circle_button_green);
                    btnCounter.setText(counter);}
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Go to Upcoming walks page. CHANGE THIS LATER TO TAB2.
        buttonTrips.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Fragment newFragment = new Tab2();
//                // consider using Java coding conventions (upper first char class names!!!)
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//
//                // Replace whatever is in the fragment_container view with this fragment,
//                // and add the transaction to the back stack
//                transaction.replace(R.id.fragment_container, newFragment);
//                transaction.addToBackStack(null);
//
//                // Commit the transaction
//                transaction.commit();


                Intent myIntent = new Intent(getContext(), Upcoming_Walks.class);

//                overridePendingTransition(0,0);
                startActivity(myIntent);
                getActivity().overridePendingTransition(0,0);

            }
        });

        //Go to map page. CHANGE THIS LATER TO TAB0.
        goBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getContext(), Map.class);

                Log.d("here here",destination.getText()+"sd");

                String destinationStr = destination.getText().toString();
                myIntent.putExtra("key", destinationStr);
                startActivity(myIntent);
            }
        });


    }




}

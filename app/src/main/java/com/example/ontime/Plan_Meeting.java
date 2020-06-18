package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ontime.AutoSuggestClasses.PlaceAutoSuggestAdapter;
import com.example.ontime.MapRelatedClasses.Map;
import com.example.ontime.MeetingsClasses.CurrentFriendsListAdapter;
import com.example.ontime.MeetingsClasses.Friend;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Plan_Meeting extends Fragment implements AdapterView.OnItemSelectedListener {

    //Initialise variables.
    Button meetBtn;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/friendRequests");
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");

    private AutoCompleteTextView destination;
    private AutoCompleteTextView autoCompleteTextView;

    String destinationConfirmed;


    String friendUId;
    ArrayList<Friend> friendsList = new ArrayList<>();
    Friend friend;
    Spinner spinner;

    List<String> tempList = new ArrayList<>();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));


//        // Spinner Drop down elements
//        List<String> categories = new ArrayList<String>();
//        categories.add("Automobile");
//        categories.add("Business Services");
//        categories.add("Computers");
//        categories.add("Education");
//        categories.add("Personal");
//        categories.add("Travel");




        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();

//        Find the friends of the current user and add them in a list.
        dbRef.child(uId).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        if (child.child("status").getValue().equals("Friends")) {
                            friendUId = child.getKey();
                            addEmailOfFriendToList(friendUId);
                        }


                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }


                }


                if (getContext() != null) {
//                    CurrentFriendsListAdapter adapter = new CurrentFriendsListAdapter(getContext(), R.layout.adapter_view_friends, friendsList);

                    Log.d("here ",tempList+"");
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, tempList);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    spinner.setAdapter(dataAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//


        //Go to map page. When the user clicks on the search button.
        meetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the destination to the Map_Meet class. This will be used to find the exact place on the map.
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
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
        spinner = v.findViewById(R.id.spinner);

        return v;
    }


    //adds the email of the friend to the tempList that is gonna be used to show all the user's friends.
    public void addEmailOfFriendToList(String friendUID){

        profRef.child(friendUID).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    String friendEmail = (String) dataSnapshot.getValue();
                    tempList.add(friendEmail);
                    Log.d("dame email list",tempList+"");
                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

    }
}
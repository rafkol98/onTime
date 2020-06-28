package com.example.ontime.MainClasses.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MapRelatedClasses.Navigate;
import com.example.ontime.MeetingsClasses.Meet_Request;
import com.example.ontime.R;
import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.example.ontime.SignIn_UpClasses.AverageSpeedNotFound;
import com.example.ontime.SignIn_UpClasses.Countdown;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * This fragment shows the upcoming walks of the user.
 */
public class Tab2 extends Fragment {

    //Initialise variables.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    ArrayList<Trip> tripList = new ArrayList<>();

    private ListView mListView;

    String destination;
    Long timestamp;
    Trip trip;
    TripListAdapter adapter;
    boolean mFlag = false;

    Button deleteBtn;

    DatabaseReference tripsRef;

    /**
     * Required empty public constructor
     */
    public Tab2() {
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_tab2, container, false);
        mListView = (ListView) v.findViewById(R.id.listView);
        deleteBtn = v.findViewById(R.id.buttonDelete);
        return v;
    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();

        tripsRef = dbRef.child(uId).child("trips");

        //Get trips of the user. Order them so that the closest one to the current date is first.
        dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        destination = child.child("destination").getValue().toString();
                        timestamp = child.child("timestamp").getValue(Long.class);
                        mFlag = (boolean) child.child("meetingFlag").getValue();


                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }


                    trip = new Trip(destination, timestamp, mFlag);
                    tripList.add(trip);
                    System.out.println("here" + tripList);

                }

                Collections.sort(tripList);
                if (getContext() != null) {
                    adapter = new TripListAdapter(getContext(), R.layout.adapter_view, tripList);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //When a user clicks on a trip open map with directions to there.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Trip selectedItem = (Trip) parent.getItemAtPosition(position);

                //Open Navigate class which shows the path to go there from his current location.
                Intent myIntent = new Intent(getContext(), Navigate.class);
                myIntent.putExtra("keyDest", selectedItem.getDestination());
                startActivity(myIntent);


            }
        });

        //On long click delete from database.
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {


                // Get the selected item text from ListView
                final Trip selectedItem = (Trip) parent.getItemAtPosition(position);

                //Make the deleteBtn visible.
                deleteBtn.setVisibility(View.VISIBLE);


                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //Initialise key for Trip and for Meeting.
                        final String keyTrip = selectedItem.getTimestamp().toString();
                        final String keyMeeting = "M" + selectedItem.getTimestamp().toString();


                        //Read the trips of the user.
                        tripsRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try {
                                    //If their trips include the keyTrip, then remove that trip.
                                    if (snapshot.hasChild(keyTrip)) {
                                        tripsRef.child(keyTrip).removeValue();
                                    }
                                    //If its not included, it means that its a meeting. Remove the meeting using keyMeeting.
                                    else {
                                        tripsRef.child(keyMeeting).removeValue();
                                    }

                                    Intent refreshIntent = new Intent(getContext(), MPage.class);
                                    refreshIntent.putExtra("Tab","Tab2");
                                    refreshIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getActivity().overridePendingTransition(0,0);
                                    startActivity(refreshIntent);





//                                    //Refresh the fragment.
//                                    Fragment newFragment = new Tab2();
//                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
//
//                                    // Replace whatever is in the fragment_container view with this fragment,
//                                    // and add the transaction to the back stack if needed
//                                    transaction.replace(R.id.fragment_container, newFragment);
//                                    transaction.addToBackStack(null);
//
//                                    // Commit the transaction
//                                    transaction.commit();


                                } catch (NullPointerException e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                });

                return true;
            }
        });



    }
}

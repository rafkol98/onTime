package com.example.ontime;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReadTrips {

    String destination;
    Long timestamp;
    Trip trip;
    int count = 0;


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    ArrayList<Trip> tripList = new ArrayList<>();

    //Get uId of the user
    final String uId = currentFirebaseUser.getUid();

    public ArrayList<Trip> getTrips() {
//        System.out.println(uId+"alo");
//
//        if (count == 0) {
            //try it again tomorrow.
            dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        destination = child.child("destination").getValue().toString();
                        timestamp = child.child("timestamp").getValue(Long.class);

                        System.out.println("HERE HERE DES"+destination+" "+timestamp);
                        trip = new Trip(destination, timestamp);
                        tripList.add(trip);
                        System.out.println(tripList);

                    }

                    Collections.sort(tripList);


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            count++;
//        }
        System.out.println("count a"+tripList.size());
        return tripList;
    }


}

package com.example.ontime;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class Friend {

    //Initialise variables.
    private String uId;
    private String email;
    private String emailFromUiD;


    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    Friend xF;

    public Friend(String uId, String email) {
        this.uId = uId;
        this.email = email;
    }

    //Initialise constructor.
    public Friend(String uId) {
        this.uId = uId;
    }


    public String getuId() {
        return uId;
    }


    public String getEmail() {
        return email;
    }



    public String getEmailFromUId() {
        dbRef.child(uId).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


//                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try{
                       email = (String) dataSnapshot.getValue();
                        Log.d("mesa dame koumpare", email+"");
                        xF = new Friend(uId,email);


                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                }
//                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Log.d("alo email", xF.getEmail()+"");
        return xF.getEmail();

    }





}

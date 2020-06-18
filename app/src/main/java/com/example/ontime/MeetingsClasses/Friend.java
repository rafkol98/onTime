package com.example.ontime.MeetingsClasses;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Friend {

    //Initialise variables.
    private String uId;
    private String email;


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


}

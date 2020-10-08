package com.example.ontime.SignIn_UpClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Collections;

public class RedoTest extends AppCompatActivity {

    //Initialise variables.
    Button letsGoBtn, laterBtn;
    TextView textSpeed;

    //Get firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_redo_test);

        //Get uId of the user from the firebase database.
        final String uId = currentFirebaseUser.getUid();

        DatabaseReference userRef = dbRef.child(uId);

        textSpeed = findViewById(R.id.textSpeed);


       userRef.child("Average Speed").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String avgSpeed = dataSnapshot.getValue().toString();

                textSpeed.setText(avgSpeed+" km/h");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Assign Buttons.
        letsGoBtn = findViewById(R.id.outsideGoNotBtn);
        laterBtn = findViewById(R.id.laterNotBtn);

        //When the user clicks letsGo he is taken to the test.
        letsGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedoTest.this, Countdown.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });

        //When the user clicks on the laterBtn he is taken to select a temporary average speed.
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RedoTest.this, SelectTempSpeed.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });
    }
}

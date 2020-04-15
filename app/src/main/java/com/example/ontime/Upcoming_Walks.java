package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Upcoming_Walks extends AppCompatActivity {
    String destination, date, time;

    SelectTime selectTime = new SelectTime();
    Object newTrip;
    Trip trip;



    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    final ArrayList<Trip> tripList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_walks);

        final ListView mListView = (ListView) findViewById(R.id.listView);


        final String uId = currentFirebaseUser.getUid();


        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.child(uId).child("trips").getChildren()) {
                    //Here you can access the child.getKey()
                    date = child.child("date").getValue().toString();
                    destination = child.child("destination").getValue().toString();
                    time = child.child("time").getValue().toString();

//                    newTrip = child.getValue();
                    trip = new Trip(destination,date,time);
//                    newTrip = new Trip(destination, date, time);
                    tripList.add(trip);

                }


                TripListAdapter adapter = new TripListAdapter(Upcoming_Walks.this, R.layout.adapter_view, tripList);
                mListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
//                String selectedItemAddress = (String) parent.getItemAtPosition(position);

                Intent myIntent = new Intent(Upcoming_Walks.this, Navigate.class);
                myIntent.putExtra("keyDest", destination);
                startActivity(myIntent);



            }
        });


    }


}





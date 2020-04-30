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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static android.widget.AdapterView.*;

public class Upcoming_Walks extends AppCompatActivity {
    String destination;
    Long timestamp;


    Trip trip;



    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    final ArrayList<Trip> tripList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_walks);

        final ListView mListView = (ListView) findViewById(R.id.listView);

        //Get uId of the user
        final String uId = currentFirebaseUser.getUid();


        //try it again tomorrow.
        dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    destination = child.child("destination").getValue().toString();
                    timestamp = child.child("timestamp").getValue(Long.class);


                    trip = new Trip(destination,timestamp);
                    tripList.add(trip);

                }

                Collections.sort(tripList);
                TripListAdapter adapter = new TripListAdapter(Upcoming_Walks.this, R.layout.adapter_view, tripList);
                mListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        //When a user clicks on a trip open map with directions there.
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Trip selectedItem = (Trip) parent.getItemAtPosition(position);



                Intent myIntent = new Intent(Upcoming_Walks.this, Navigate.class);
                myIntent.putExtra("keyDest", selectedItem.getDestination());
                startActivity(myIntent);



            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {

                //need to remove it from the database.

                return false;
            }
        });

    }


}





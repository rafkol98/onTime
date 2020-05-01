package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static android.widget.AdapterView.*;

public class Upcoming_Walks extends AppCompatActivity {
    String destination;
    Long timestamp;

    TripListAdapter adapter;
    Trip trip;

    Button deleteBtn;


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    final ArrayList<Trip> tripList = new ArrayList<>();
    final ArrayList<String> keyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_walks);

        final ListView mListView = (ListView) findViewById(R.id.listView);
        deleteBtn = findViewById(R.id.buttonDelete);

        //Get uId of the user
        final String uId = currentFirebaseUser.getUid();


        //try it again tomorrow.
        dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    destination = child.child("destination").getValue().toString();
                    timestamp = child.child("timestamp").getValue(Long.class);


                    trip = new Trip(destination, timestamp);
                    tripList.add(trip);
                    keyList.add(child.getKey());

                }

                Collections.sort(tripList);
                adapter = new TripListAdapter(Upcoming_Walks.this, R.layout.adapter_view, tripList);
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

                DateTimeCheck dateTimeCheck = new DateTimeCheck();
                // Get the selected item text from ListView
                final Trip selectedItem = (Trip) parent.getItemAtPosition(position);

                String dateTrip = dateTimeCheck.convertTime(selectedItem.getTimestamp());
                Date currentDate = Calendar.getInstance().getTime();
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                String currentStrDate = dateFormat.format(currentDate);

                if(dateTimeCheck.startEarlier(new SimpleDateFormat("dd/MM/yyyy HH:mm"),currentStrDate,dateTrip)){
                    final AlertDialog.Builder builder = new AlertDialog.Builder(Upcoming_Walks.this);
                    builder.setMessage("You are about to start the walk earlier than expected, are you sure you want to proceed?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    Intent myIntent = new Intent(Upcoming_Walks.this, Navigate.class);
                                    myIntent.putExtra("keyDest", selectedItem.getDestination());
                                    startActivity(myIntent);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(final DialogInterface dialog, final int id) {
                                    dialog.dismiss();
                                }
                            });
                    final AlertDialog alert = builder.create();
                    alert.show();
                }





            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {
                //Set long button visible
                deleteBtn.setVisibility(VISIBLE);

                //on click of delete button
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Trip item = adapter.getItem(position);


                        dbRef.child(uId).child("trips").child(keyList.get(position)).removeValue();

                        keyList.remove(position);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        mListView.setAdapter(adapter);


                        deleteBtn.setVisibility(INVISIBLE);

                    }
                });

                return true;
            }
        });


    }

    public void onBackPressed() {
        Intent myIntent = new Intent(Upcoming_Walks.this, MPage.class);
//add a slide back transition. Maybe slidr
        startActivity(myIntent);
    }


}





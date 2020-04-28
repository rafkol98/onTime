package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment {


    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    ArrayList<Trip> tripList = new ArrayList<>();
    ReadTrips readTrips = new ReadTrips();


    public Tab2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab2, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

        final ListView mListView = (ListView) v.findViewById(R.id.listView);

        //Get uId of the user
        final String uId = currentFirebaseUser.getUid();

        tripList=readTrips.getTrips();
        TripListAdapter adapter = new TripListAdapter(getContext(), R.layout.adapter_view, tripList);
        mListView.setAdapter(adapter);

        //When a user clicks on a trip open map with directions there.
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                Trip selectedItem = (Trip) parent.getItemAtPosition(position);


                Intent myIntent = new Intent(getContext(), Navigate.class);
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

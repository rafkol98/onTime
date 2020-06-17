package com.example.ontime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ontime.MainClasses.FriendsReqListAdapter;
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

/**
 * This fragment is used to show all the friends of a user.
 */
public class Your_Friends extends Fragment {

    //Initialise variables.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/friendRequests");


    String friendUId;
    ArrayList<Friend> friendsList = new ArrayList<>();
    private ListView reqListView;

    ImageView imgFriendsAppear;

    Friend friend;



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();
        //Get trips of the user. Order them so that the closest one to the current date is first.
        dbRef.child(uId).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        if (child.child("status").getValue().equals("Friends")) {
                            imgFriendsAppear.setVisibility(View.INVISIBLE);
                            friendUId = child.getKey();
                            friend = new Friend(friendUId);
                            friendsList.add(friend);
                        }


                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }


                }


                if (getContext() != null) {
                    CurrentFriendsListAdapter adapter = new CurrentFriendsListAdapter(getContext(), R.layout.adapter_view_friends, friendsList);
                    reqListView.setAdapter(adapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_your_friends, container, false);
        reqListView = (ListView) v.findViewById(R.id.listView_your_friends);
        imgFriendsAppear = v.findViewById(R.id.newFriend_appear);

        return v;
    }
}
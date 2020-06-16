package com.example.ontime.MeetingsClasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ontime.MainClasses.FriendsReqListAdapter;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendRequests extends Fragment {

    public FriendRequests() {
        // Required empty public constructor
    }

    //Initialise variables.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/friendRequests");


    String friendUId;
    ArrayList<Friend> friendsList = new ArrayList<>();
    private ListView reqListView;

    Button acceptReq;

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
                        if (child.child("status").getValue().equals("Received")) {
                            friendUId = child.getKey();
                            friend = new Friend(friendUId);
                            friendsList.add(friend);
                        }

                        // && friendsList.contains(child.getKey()
                        else if (child.child("status").getValue().equals("Friends")) {
                            Fragment newFragment = new FriendRequests();
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                            // Replace whatever is in the fragment_container view with this fragment,
                            // and add the transaction to the back stack if needed
                            transaction.replace(R.id.fragment_container, newFragment);
                            transaction.addToBackStack(null);

                            // Commit the transaction
                            transaction.commit();
//                            friendsList.remove(child.getKey());
                        }

                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }


                }


                if (getContext() != null) {
                    FriendsReqListAdapter adapter = new FriendsReqListAdapter(getContext(), R.layout.adapter_view_req, friendsList);
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
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friend_requests, container, false);
        reqListView = (ListView) v.findViewById(R.id.listView_req);
        return v;

    }



}
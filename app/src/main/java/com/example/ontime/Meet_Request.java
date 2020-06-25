package com.example.ontime;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ontime.MainClasses.FriendsReqListAdapter;
import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MeetingsClasses.Friend;
import com.example.ontime.MeetingsClasses.Meeting;
import com.example.ontime.MeetingsClasses.MeetingRequestsListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Meet_Request extends Fragment {


    //Initialise variables.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");

    ArrayList<Meeting> meetingList = new ArrayList<>();

    private ListView mListView;

    String senderUid, destination, senderEmail;

    Long timestamp;

    Meeting newMeetingReq;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();

        //Find users, from who the current user "Received" a friend request and add them in the list.
        profRef.child(uId).child("meeting_request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        destination = child.child("destination").getValue().toString();
                        senderUid = child.child("uIdSender").getValue().toString();
                        timestamp = child.child("timestamp").getValue(Long.class);

                        System.out.println("dame to debug tou meeting  dest"+destination+"  sid"+senderUid+"  t"+timestamp);

                        newMeetingReq = new Meeting(destination, timestamp, senderUid);
                        meetingList.add(newMeetingReq);


//                        profRef.child(senderUid).addValueEventListener(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                try {
//                                    senderEmail = dataSnapshot.child("Email").getValue().toString();
//
//                                    System.out.println("dame to debug tou senderEmail  "+senderEmail);
//
//
//
//
//
//
//                                } catch (NullPointerException e) {
//                                    FirebaseCrashlytics.getInstance().recordException(e);
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                            }
//                        });




                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                }





                if (getContext() != null) {
                    MeetingRequestsListAdapter adapter = new MeetingRequestsListAdapter(getContext(), R.layout.adapter_view_meet_req, meetingList);
                    System.out.println("ipotithete ekamato");
                    mListView.setAdapter(adapter);
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
        View v = inflater.inflate(R.layout.fragment_meet_request, container, false);
        mListView = (ListView) v.findViewById(R.id.listView_req_meetings);
        return v;
    }
}
package com.example.ontime.MeetingsClasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ontime.MainClasses.FriendsReqListAdapter;
import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MeetingsClasses.Friend;
import com.example.ontime.MeetingsClasses.Meeting;
import com.example.ontime.MeetingsClasses.MeetingRequestsListAdapter;
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


public class Meet_Request extends Fragment {


    //Initialise variables.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");

    ArrayList<Trip> meetingList = new ArrayList<>();

    private ListView mListView;

    String senderUid, destination, senderEmail;

    Long timestamp;

    Trip newMeetingReq;

    ImageView noMeetings_img;

    double desLatitude, desLongitude;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();

        //Find all the meeting requests the user has.
        profRef.child(uId).child("meeting_request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {

                    try {
                        destination = child.child("destination").getValue().toString();
                        senderUid = child.child("senderUId").getValue().toString();
                        timestamp = child.child("timestamp").getValue(Long.class);
                        desLatitude = child.child("latitude").getValue(Double.class);
                        desLongitude = child.child("longitude").getValue(Double.class);

                        newMeetingReq = new Trip(destination, desLatitude, desLongitude, timestamp, senderUid,true);
                        meetingList.add(newMeetingReq);

                        Log.d("I am in the fb","Meeting list size "+meetingList.size()+"meeting list first element "+meetingList.get(0).getDestination());
                        noMeetings_img.setVisibility(View.INVISIBLE);

                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
                if (getContext() != null) {
                    MeetingRequestsListAdapter adapter = new MeetingRequestsListAdapter(getContext(), R.layout.adapter_view_meet_req, meetingList);
                    mListView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                FirebaseCrashlytics.getInstance().log(databaseError.getDetails());
            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View v = inflater.inflate(R.layout.fragment_meet_request, container, false);
        mListView = (ListView) v.findViewById(R.id.listView_req_meetings);
        noMeetings_img = v.findViewById(R.id.noMeetings_Img);
        return v;
    }
}
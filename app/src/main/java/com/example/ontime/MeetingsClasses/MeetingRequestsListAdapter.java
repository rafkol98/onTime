package com.example.ontime.MeetingsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ontime.MainClasses.Trip;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MeetingRequestsListAdapter extends ArrayAdapter<Trip> {


    private Context mContext;
    int mResource;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");
    String uId = currentFirebaseUser.getUid();


    public MeetingRequestsListAdapter(@NonNull Context context, int resource, @NonNull List<Trip> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    // Default values in case getItem().get... produces a NPE
    String friendUId = "";
    String destination = "";
    Long timestamp = 0L;


    /**
     * Inflate the View and return it
     *
     * @param position    - Destination to get
     * @param convertView -
     * @param parent      ViewGroup of parent
     * @return view
     */
    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final Trip meeting = getItem(position);

        // Attempt to get the destination
        try {
            friendUId = meeting.getSenderUId();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        try {
            destination = meeting.getDestination();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        try {
            timestamp = meeting.getTimestamp();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent, false);


        // Obtain views by ID
        TextView tvPlaceTime = (TextView) convertView.findViewById(R.id.textPlacedTime);



        // Convert milliseconds to human readable form
        String time=convertTime(timestamp);


        // Set the destination to the TextView
        tvPlaceTime.setText(destination +" at: "+ time);

        final TextView tvInvitedBy = (TextView) convertView.findViewById(R.id.textInvitedBy);

                        profRef.child(friendUId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                try {
                                    String senderEmail = dataSnapshot.child("Email").getValue().toString();

                                    // Set the email of the sender
                                    tvInvitedBy.setText("By: "+ senderEmail);


                                } catch (NullPointerException e) {
                                    FirebaseCrashlytics.getInstance().recordException(e);
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                                FirebaseCrashlytics.getInstance().log(databaseError.getDetails());
                            }
                        });





        Button acceptReq = convertView.findViewById(R.id.btnAcceptMeet);
        Button rejectReq = convertView.findViewById(R.id.btnRejectMeet);



        //When a user clicks the accept button, make both status as friends.
        acceptReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Write the meeting under user's trips.
                DatabaseReference childReff = profRef.child(uId).child("trips").child(meeting.getTimestamp().toString());
                childReff.setValue(meeting, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            FirebaseCrashlytics.getInstance().log(databaseError.getMessage());
                            FirebaseCrashlytics.getInstance().log(databaseError.getDetails());
                        } else {
                            FirebaseCrashlytics.getInstance().log("Successful write of Meeting");
                        }
                    }
                });

                //remove userUid from friendRequests of the friend.
                DatabaseReference meetRef = profRef.child(uId).child("meeting_request");
                meetRef.child(meeting.getTimestamp().toString()).removeValue();

                //Refresh the fragment.
                Fragment newFragment = new Meet_Request();
                FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
                
            }
        });

        rejectReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //remove userUid from friendRequests of the friend.
                DatabaseReference meetRef = profRef.child(uId).child("meeting_request");
                meetRef.child(meeting.getTimestamp().toString()).removeValue();

                //Refresh the fragment.
                Fragment newFragment = new Meet_Request();
                FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });






        // Return the inflated view
        return convertView;
    }




    /**
     * Convert time from Long to Human readable date format.
     * @param time in milliseconds
     * @return time formatted with date time format
     */
    public String convertTime(long time){
        // Convert the millisecond based time to a Date object
        Date date = new Date(time);

        // Create a new instance of the SimpleDateFormat
        // TODO:: Implement Locale for users given device settings
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        // Return the formatted Date object
        return format.format(date);
    }

}

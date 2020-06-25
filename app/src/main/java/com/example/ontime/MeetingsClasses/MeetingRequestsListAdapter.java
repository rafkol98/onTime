package com.example.ontime.MeetingsClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class MeetingRequestsListAdapter extends ArrayAdapter<Meeting> {


    private Context mContext;
    int mResource;

//    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
//    String uId = currentFirebaseUser.getUid();


    public MeetingRequestsListAdapter(@NonNull Context context, int resource, @NonNull List<Meeting> objects) {
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
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        System.out.println("empika mesa sto list adapter");

        // Attempt to get the destination
        try {
            friendUId = getItem(position).getuIdSender();
            System.out.println(friendUId+" alo debug alo");
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        try {
            destination = getItem(position).getDestination();
            System.out.println(destination+" alo debug alo");
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        try {
            timestamp = getItem(position).getTimestamp();
            System.out.println(timestamp+" alo debug alo");
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent, false);

        // Obtain view by ID
        final TextView tvFriend = (TextView) convertView.findViewById(R.id.textFriend);

        // Obtain views by ID
        TextView tvPlaceTime = (TextView) convertView.findViewById(R.id.textPlacedTime);
        TextView tvInvitedBy = (TextView) convertView.findViewById(R.id.textInvitedBy);


        // Convert milliseconds to human readable form
        String time=convertTime(timestamp);

        System.out.println("time here inside listAdapter"+time);

        // Set the destination to the TextView
        tvPlaceTime.setText(destination +" at: "+ time);

        // Set the email of the sender
        tvInvitedBy.setText("Invited by: "+ friendUId);



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

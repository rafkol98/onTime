package com.example.ontime.MainClasses;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ontime.R;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Adapter used to store in a list all the upcoming trips and all the necessary information about
 * each trip.
 */
public class TripListAdapter extends ArrayAdapter<Trip> {

    private Context mContext;
    int mResource;

    /**
     * Constructor for TripListAdapter to
     * @param context for the trip list adapter
     * @param resource
     * @param objects - List of trips
     */
    public TripListAdapter(@NonNull Context context, int resource, List<Trip> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    /**
     * Inflate the View and return it
     * @param position - Destination to get
     * @param convertView -
     * @param parent ViewGroup of parent
     * @return view
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get destination and time.
        // Default values in case getItem().get... produces a NPE
        String destination = "";
        Long timeLong = 0L;
        boolean isMeeting=false;

        // Attempt to get the destination
        try{
            destination = getItem(position).getDestination();
        } catch (NullPointerException e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // Attempt to get the time
        try{
            timeLong = getItem(position).getTimestamp();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // Attempt to understand if its a meeting.
        try{
            isMeeting = getItem(position).isMeetingFlag();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent,false);

        // Obtain views by ID
        TextView tvDestination = (TextView) convertView.findViewById(R.id.textDestination);
        TextView tvDate = (TextView) convertView.findViewById(R.id.textPlacedTime);
        TextView tvMeeting = (TextView) convertView.findViewById(R.id.textInvitedBy);

        // Convert milliseconds to human readable form
        String time=convertTime(timeLong);

        // Set the destination to the TextView
        tvDestination.setText(destination);

        // Set the converted time to the date TextView
        tvDate.setText(time);


        if(isMeeting){
            tvMeeting.setText("Meeting");
        } else {
            tvMeeting.setText("");
        }


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

package com.example.ontime;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.ValueEventListener;

import java.security.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TripListAdapter extends ArrayAdapter<Trip> {

    private Context mContext;
    int mResource;


    public TripListAdapter(@NonNull Context context, int resource, List<Trip> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get destination and time.
        String destination = getItem(position).getDestination();
        Long timeLong = getItem(position).getTimestamp();


        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

        TextView tvDestination = (TextView) convertView.findViewById(R.id.textDestination);
        TextView tvDate = (TextView) convertView.findViewById(R.id.textDate);
        TextView tvTime = (TextView) convertView.findViewById(R.id.textTime);

       String time=convertTime(timeLong);

        tvDestination.setText(destination);
        tvDate.setText(time);
        tvTime.setText("");



        return convertView;
    }

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }
}

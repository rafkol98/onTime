package com.example.ontime.MainClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ontime.Friend;
import com.example.ontime.R;
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

public class FriendsListAdapter extends ArrayAdapter<Friend> {

    private Context mContext;
    int mResource;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    public FriendsListAdapter(@NonNull Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    // Default values in case getItem().get... produces a NPE
    String friendUid = "";
    String friendEmail = "";

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

        // Attempt to get the destination
        try{
            friendUid = getItem(position).getuId();
            Log.d("I progressed here a",friendUid+"");
        } catch (NullPointerException e){
            FirebaseCrashlytics.getInstance().recordException(e);
        }


        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent,false);

        // Obtain view by ID
        final TextView tvFriend = (TextView) convertView.findViewById(R.id.textFriend);


        dbRef.child(friendUid).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try{
                    friendEmail = (String) dataSnapshot.getValue();
                    Log.d("mesa dame koumpare", friendEmail+"");
                    // Set friendEmail to the text view.
                    tvFriend.setText(friendEmail);


                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

            }
//                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }



        });





        // Return the inflated view
        return convertView;
    }


}

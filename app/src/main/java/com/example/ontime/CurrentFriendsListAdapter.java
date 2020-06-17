package com.example.ontime;

import android.content.Context;
import android.util.Log;
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

import com.example.ontime.MeetingsClasses.Friend;
import com.example.ontime.MeetingsClasses.FriendRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CurrentFriendsListAdapter extends ArrayAdapter<Friend> {


    private Context mContext;
    int mResource;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    private DatabaseReference dbRefFriend = FirebaseDatabase.getInstance().getReference("/friendRequests");
    String uId = currentFirebaseUser.getUid();

    public CurrentFriendsListAdapter(@NonNull Context context, int resource, List<Friend> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
    }

    // Default values in case getItem().get... produces a NPE
    String friendUid = "";
    String friendEmail = "";


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


        // Attempt to get the destination
        try {
            friendUid = getItem(position).getuId();
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }


        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent, false);

        // Obtain view by ID
        final TextView tvFriend = (TextView) convertView.findViewById(R.id.textFriend);


        dbRef.child(friendUid).child("Email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    friendEmail = (String) dataSnapshot.getValue();
                    // Set friendEmail to the text view.
                    tvFriend.setText(friendEmail);


                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        // Return the inflated view
        return convertView;
    }

}

package com.example.ontime.MainClasses;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ontime.MeetingsClasses.AddFriend;
import com.example.ontime.MeetingsClasses.Friend;
import com.example.ontime.MeetingsClasses.FriendRequests;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FriendsReqListAdapter extends ArrayAdapter<Friend> {

    private Context mContext;
    int mResource;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    private DatabaseReference dbRefFriend = FirebaseDatabase.getInstance().getReference("/friendRequests");
    String uId = currentFirebaseUser.getUid();

    public FriendsReqListAdapter(@NonNull Context context, int resource, List<Friend> objects) {
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
            Log.d("I progressed here a", friendUid + "");
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }


        // Obtain LayoutInflator instance from the stored context
        LayoutInflater inflater = LayoutInflater.from(mContext);

        // Inflate the layout with the given resource and parent viewGroup
        convertView = inflater.inflate(mResource, parent, false);

        // Obtain view by ID
        final TextView tvFriend = (TextView) convertView.findViewById(R.id.textFriend);


        Button acceptReq = convertView.findViewById(R.id.btnAccept);
        Button rejectReq = convertView.findViewById(R.id.btnReject);


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
//                }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        //When a user clicks the accept button, make both status as friends.
        acceptReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference userRef = dbRefFriend.child(uId);
                userRef.child("friends").child(friendUid).child("status").setValue("Friends");

                //write to the friend. We will write under the node "friends" the uId of the current user along with the status as received, as the
                //friend has received this request.
                DatabaseReference friendsRef = dbRefFriend.child(friendUid);
                friendsRef.child("friends").child(uId).child("status").setValue("Friends");


                //After they successfully become friends, refresh the fragment.
                Fragment newFragment = new FriendRequests();
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
                //remove friendUid from friendRequests of user.
                DatabaseReference userRef = dbRefFriend.child(uId);
                userRef.child("friends").child(friendUid).removeValue();

                //remove userUid from friendRequests of the friend.
                DatabaseReference friendsRef = dbRefFriend.child(friendUid);
                friendsRef.child("friends").child(uId).removeValue();

                //Refresh the fragment.
                Fragment newFragment = new FriendRequests();
                FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });


        //TODO button delete.


        // Return the inflated view
        return convertView;
    }


}

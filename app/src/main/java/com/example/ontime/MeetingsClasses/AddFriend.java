package com.example.ontime.MeetingsClasses;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ontime.MainClasses.HashEmail;
import com.example.ontime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class AddFriend extends Fragment {

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRefEmail = FirebaseDatabase.getInstance().getReference("/emailToUid");
    private DatabaseReference dbRefFriend = FirebaseDatabase.getInstance().getReference("/friendRequests");
    String uId = currentFirebaseUser.getUid();

    EditText emailIn;
    HashEmail hashEmail = new HashEmail();

    String friendUid;

    Button sendButton;

    //temporary String flag. I set this flag to the value of friendUid once I send an invite to a friend. This solved a complex problem
    //that because the data was changed the values were reading again and again on the onDataChanged so both toasts were printed.
    String flag;

    public AddFriend() {
        // Required empty public constructor
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Initialise view.
        View v = getView();
        //Get email passed in.
        emailIn = (EditText) v.findViewById(R.id.emailPassedIn);



        sendButton = v.findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get text the user typed.
                String emailValue = emailIn.getText().toString();

                //get the hash key of the email passed in.
                final String hashValue = hashEmail.getHashEmail(emailValue);
                Log.d("Hash key Log", hashValue);

                //READ VALUE, FIND UID OF USER WITH EMAIL.
                dbRefEmail.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            String childKey = child.getKey();
                            if (childKey.equals(hashValue)) {
                                friendUid = (String) child.getValue();
                            }
                        }
                        if(friendUid!=null && friendUid.equals(uId) ){
                            Toast.makeText(getContext(), "You cannot add yourself as a friend", Toast.LENGTH_LONG).show();
                        } else if (friendUid == null) {
                            Toast.makeText(getContext(), "We couldn't find your friend's email in our database, make sure you typed it correctly", Toast.LENGTH_LONG).show();
                        } else {
                            //We need to do 2 writes, one for the current user. and one for the friend. Write to each other the uid of the other
                            //and the request status.

                            dbRefFriend.addValueEventListener(new ValueEventListener() {


                             @Override
                             public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                 String statusOfUser = (String) dataSnapshot.child(uId).child("friends").child(friendUid).child("status").getValue();
                                 String statusOfFriend = (String) dataSnapshot.child(friendUid).child("friends").child(uId).child("status").getValue();

                                 Log.d("statuses 2", statusOfFriend + "  " + statusOfUser);


                                 if(statusOfFriend!=null && statusOfUser!=null && flag==friendUid){
                                     Toast.makeText(getContext(), "You already made a connection with that email.", Toast.LENGTH_LONG).show();
                                 }else {
                                     Log.d("I went inside", "alo dame");
                                     //write to the current user. We will write under the node "friends" the uId of the friend along with the status as sent, as the user is
                                     // the one who sent the request.
                                     DatabaseReference userRef = dbRefFriend.child(uId);
                                     userRef.child("friends").child(friendUid).child("status").setValue("Sent");

                                     //write to the friend. We will write under the node "friends" the uId of the current user along with the status as received, as the
                                     //friend has received this request.
                                     DatabaseReference friendsRef = dbRefFriend.child(friendUid);
                                     friendsRef.child("friends").child(uId).child("status").setValue("Received");

                                     Toast.makeText(getContext(), "Friend request sent succesfully", Toast.LENGTH_LONG).show();
                                     emailIn.setText("");

                                     //set the flag to friendUid.
                                     flag=friendUid;

                                 }


                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError databaseError) {

                             }
                            }
                            );

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_friend, container, false);
    }
}
package com.example.ontime;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.example.ontime.AutoSuggestClasses.PlaceAutoSuggestAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class Plan_Meeting extends Fragment {

    //Initialise variables.
    Button meetBtn, selectBtn;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/friendRequests");
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");

    private AutoCompleteTextView destination;
    private AutoCompleteTextView autoCompleteTextView;

    String destinationConfirmed;

//    SetUniqueList


    String friendUId;


    ArrayList<String> friendsList = new ArrayList<>();
    ArrayList<String> friendsCopy;
    List<Boolean> booleansList = new ArrayList<>();

    ArrayList<String> uIdList = new ArrayList<>();


    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));

        //Get uId of the user from the database.
        final String uId = currentFirebaseUser.getUid();

        //Find the friends of the current user and add them in a list.
        dbRef.child(uId).child("friends").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    try {
                        if (child.child("status").getValue().equals("Friends")) {
                            friendUId = child.getKey();
                            uIdList.add(friendUId);
                            booleansList.add(false);
                        }
                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }

                //Use the getEmailsOfAllFriends method to get the email of all the friends using their uId.
                final ArrayList<String> emailsOfAllFriends = (ArrayList) getEmailsOfAllFriends(uIdList);

                //When the user clicks "select" open the alert dialog to enable the user to select the friends he want to share the trip with.
                selectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] finalArrayFriends = toPrimitiveArrayString(emailsOfAllFriends);


                        final boolean[] arrayBoolean = toPrimitiveArray(booleansList);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder.setCancelable(true);
                        alertDialogBuilder.setTitle("Select friends to share the meeting");
                        alertDialogBuilder.setMultiChoiceItems(finalArrayFriends, arrayBoolean, new DialogInterface.OnMultiChoiceClickListener() {
                            //When the user ticks on an email, add them on the list.
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                if (isChecked) {
                                    //add the item if it doesn't exist in the list.
                                    if (!friendsList.contains(emailsOfAllFriends.get(which))) {
                                        friendsList.add(emailsOfAllFriends.get(which));
                                    }
                                }
                            }
                        });

                        //When the user clicks confirm, add all the selected items in the friendsCopy.
                        alertDialogBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                System.out.println("Friends list elements" + friendsList);

                                //Create a new copied array from the elements selected.
                                friendsCopy = new ArrayList<>(friendsList);

                                for (int i = 0; i < friendsList.size(); i++) {
                                    friendsList.remove(i);
                                }

                            }
                        });

                        Log.d("friends copy list",friendsCopy+"");

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.setCanceledOnTouchOutside(true);
                        alertDialog.show();


                    }
                });

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//


        // Open a map when the user clicks on the search button.
        meetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pass the destination to the Map_Meet class. This will be used to find the exact place on the map.
                String destinationStr = destination.getText().toString();

                Bundle bundle = new Bundle();
                bundle.putString("keyMeeting", destinationStr); // Put anything what you want

                Fragment fragmentMap = new Map_Meet();
                fragmentMap.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, fragmentMap);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();


            }
        });


        Bundle bundle = this.getArguments();
        //If the user confirmed the meeting (by clicking confirm on the map), then set the text of the AutocompleteTextView
        //as the destination.
        if (bundle != null) {
            destinationConfirmed = getArguments().getString("confirmedMeeting");
        }

        destination.setText(destinationConfirmed);


    }


    //To convert boolean list to array boolean.
    private boolean[] toPrimitiveArray(final List<Boolean> booleanList) {
        final boolean[] primitives = new boolean[booleanList.size()];
        int index = 0;
        for (Boolean object : booleanList) {
            primitives[index++] = object;
        }
        return primitives;
    }

    private String[] toPrimitiveArrayString(final List<String> stringsList) {
        final String[] primitives = new String[stringsList.size()];
        int index = 0;
        for (String object : stringsList) {
            primitives[index++] = object;
        }
        return primitives;
    }


    public static String[] GetStringArray(ArrayList<String> arr) {
        // declaration and initialise String Array
        String str[] = new String[arr.size()];

        // ArrayList to Array Conversion
        for (int j = 0; j < arr.size(); j++) {
            // Assign each value to String array
            str[j] = arr.get(j);
        }
        return str;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan_meeting, container, false);

        //initialise buttons and variables used.
        meetBtn = v.findViewById(R.id.meet_btn);
        destination = v.findViewById(R.id.meetingAutoComplete);
        autoCompleteTextView = v.findViewById(R.id.meetingAutoComplete);
        selectBtn = v.findViewById(R.id.select_Btn);
//        spinner = v.findViewById(R.id.spinner);

        return v;
    }


    public List<String> getEmailsOfAllFriends(List<String> listIn) {

        final List<String> listWithEmails = new ArrayList<>();

        for (int i = 0; i < listIn.size(); i++) {

            profRef.child(listIn.get(i)).child("Email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    try {
                        String friendEmail = (String) dataSnapshot.getValue();
                        listWithEmails.add(friendEmail);
                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        return listWithEmails;
    }


}
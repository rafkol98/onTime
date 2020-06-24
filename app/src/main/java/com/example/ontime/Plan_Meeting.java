package com.example.ontime;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ontime.AutoSuggestClasses.PlaceAutoSuggestAdapter;
import com.example.ontime.DateTimeClasses.DateTimeCheck;
import com.example.ontime.DateTimeClasses.SelectTime;
import com.example.ontime.DateTimeClasses.TimePickerFragment;
import com.example.ontime.MainClasses.HashEmail;
import com.example.ontime.MainClasses.Trip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class Plan_Meeting extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    //Initialise variables.
    Button meetBtn, selectBtn, planBtn;

    TextView dateText, timeText;

    ImageView dateImg, timeImg;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/friendRequests");
    private DatabaseReference profRef = FirebaseDatabase.getInstance().getReference("/profiles");
    private DatabaseReference dbRefEmail = FirebaseDatabase.getInstance().getReference("/emailToUid");

    private AutoCompleteTextView destination;
    private AutoCompleteTextView autoCompleteTextView;

    String destinationConfirmed;

    String friendUId;


    ArrayList<String> friendsList = new ArrayList<>();
    ArrayList<String> friendsCopy;
    List<Boolean> booleansList = new ArrayList<>();

    ArrayList<String> uIdList = new ArrayList<>();

    ArrayList<String> selectedUIdList = new ArrayList<>();

    HashEmail hashEmail = new HashEmail();

    DateTimeCheck dateTimeCheck = new DateTimeCheck();

    String date;


    boolean clicked = false;  //used as a flag for the select button.
    
    Meeting meeting;


    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(getContext(), android.R.layout.simple_list_item_1));
        destination.setText("");

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
                        clicked = true;
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

                                //Create a new copied array from the elements selected.
                                friendsCopy = new ArrayList<>(friendsList);

                                for (int i = 0; i < friendsList.size(); i++) {
                                    friendsList.remove(i);
                                }

                            }
                        });

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

        //When click on the image of the diary, open dialog that allows the user to select date.
        dateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //When click on the text of the date, open dialog that allows the user to select date.
        dateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        //When click on the image of the clock, open dialog that allows the user to select time.
        timeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });

        //When click on the text of the time, open dialog that allows the user to select time.
        timeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });


        //When the plan button is clicked execute.
        planBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                //TODO
                //1. GET THE USERS SELECTED. GET UID FROM EMAIL FROM DATABASE.  --xx--
                //2. CREATE NEW CHILD "MEETINGS" -> CHILD (HASH MEETING): MEETING(TRIP, UID OF SENDER)
                //3. CREATE NEW CLASS MEETING(TRIP,SENDER UID). STORE NEW MEETING UNDER 'TRIPS' OF CURRENT UID. WRITE 'MEETING' IN BOTTOM COLUMN.
                //4.
                System.out.println("dame to destination ela" + destination.getText().toString());

                if(validate()) {


                    dbRefEmail.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            //Get the users that were selected and get their UId from the database.
                            for (int i = 0; i < friendsCopy.size(); i++) {
                                String hashValue = hashEmail.getHashEmail(friendsCopy.get(i));
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String childKey = child.getKey();

                                    //if the email of the user == childKey, add the Uid of user to a new ArrayList.
                                    if (childKey.equals(hashValue)) {
                                        selectedUIdList.add((String) child.getValue());
                                    }
                                }

                                System.out.println("selected list size: " + selectedUIdList.size() + " first element: " + selectedUIdList.get(0));
                            }

                            //Get dateSelected.
                            String dateSelected = dateText.getText().toString() + " " + timeText.getText().toString();


                            try {
                                Long timestamp = dateTimeCheck.toMilli(dateSelected);
                                // Create a new Meeting.
                                meeting = new Meeting(destination.getText().toString(), timestamp,uId);

                                //Store the meeting on Firebase RealTime Database.
                                DatabaseReference childReff = profRef.child(uId).child("trips").child(meeting.getMeetingId());
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



                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });


    }

    //Shows the date picker dialog.
    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), R.style.MyDatePickerDialogTheme, this, Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();

    }

    //Shows the time picker dialog.
    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), R.style.MyTimePickerDialogTheme, this, Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE), DateFormat.is24HourFormat(getContext()));
        timePickerDialog.show();
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
        dateText = v.findViewById(R.id.date_sel_text);
        timeText = v.findViewById(R.id.time_sel_text);
        dateImg = v.findViewById(R.id.dateImg);
        timeImg = v.findViewById(R.id.timeImg);
        planBtn = v.findViewById(R.id.buttonPlan);


        return v;
    }

    //returns emails of all friends.
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


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        dateImg.setVisibility(View.INVISIBLE);
        date = dayOfMonth + "/" + (month + 1) + "/" + year;
        dateText.setText(date);
        Log.d("date selected", date);
        dateText.setVisibility(View.VISIBLE);

    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        timeImg.setVisibility(View.INVISIBLE);

        Log.d("time selected", hour + ":" + minute);
        timeText.setText(String.format("%02d:%02d", hour, minute));

        timeText.setVisibility(View.VISIBLE);
    }

    //Validates that all the required inputs are being placed.
    public boolean validate() {
        boolean x = true;
        if (destination.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_LONG).show();
            x=false;
        }
        if (!clicked) {
            Toast.makeText(getContext(), "Select someone to share this meeting with", Toast.LENGTH_LONG).show();
            x=false;
        }
        if ((dateText.getText().toString()).equalsIgnoreCase("date") || (timeText.getText().toString()).equalsIgnoreCase("time")) {
            Toast.makeText(getContext(), "Please select both date and time!", Toast.LENGTH_LONG).show();
            x=false;
        }
        return x;
    }

}
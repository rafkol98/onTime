package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Menu extends AppCompatActivity {

    private AutoCompleteTextView destination;
    Button btnCounter;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnCounter = findViewById(R.id.button_counter);
//        Drawable btn_bg_green = findViewById(R.drawable.circle_button_green);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoComplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(Menu.this, android.R.layout.simple_list_item_1));

        final String uId = currentFirebaseUser.getUid();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long childrenL = dataSnapshot.child(uId).child("trips").getChildrenCount();
                String counter = Long.toString(childrenL);
                if (childrenL != 0){
                btnCounter.setBackgroundResource(R.drawable.circle_button_green);
                btnCounter.setText(counter);}
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void onBackPressed(){

    }


    public void onClickB(View v) {
        Intent myIntent = new Intent(Menu.this, Map.class);
        destination = findViewById(R.id.autoComplete);

        String destinationStr = destination.getText().toString();
        myIntent.putExtra("key", destinationStr);
        startActivity(myIntent);
    }

    public void onClickTemp(View v) {
        Intent myIntent = new Intent(Menu.this, MPage.class);
        startActivity(myIntent);
    }

    public void onUpcoming(View v) {

        Intent myIntent = new Intent(Menu.this, Upcoming_Walks.class);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            myIntent.putExtras(getIntent().getExtras());
        }
        startActivity(myIntent);
    }


}





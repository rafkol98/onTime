package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import java.util.List;

public class Menu extends AppCompatActivity {
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

    private EditText text;
    private AutoCompleteTextView destination;
     Button walks;
     String addressToRet;


     Upcoming_Walks upcoming_walks = new Upcoming_Walks();



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoComplete);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(Menu.this,android.R.layout.simple_list_item_1));

//        addressToRet = autoCompleteTextView.getText().toString();


//        upcoming_walks. itemsAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_list_item_1, items);
//        addListenerOnButton();



    }




    //    public void onBtn(View v) {
//        text = (EditText)findViewById(R.id.writePlace);
//        String string =text.getText().toString();
//
//        upcoming_walks.getItemsAdapter().add(string);
//
//        upcoming_walks.writeItems();
//
//
//    }

    public void onClickB(View v){
        Intent myIntent = new Intent(Menu.this,Map.class);
        destination = findViewById(R.id.autoComplete);

        String destinationStr = destination.getText().toString();
        myIntent.putExtra("key",destinationStr);
        startActivity(myIntent);
    }




//    public void addListenerOnButton() {
//        Intent myIntent = new Intent(Menu.this,Map.class);
//                startActivity(myIntent);
//


//        walks = findViewById(R.id.buttonTrips);
//        walks.setOnClickListener(new View.OnClickListener() {
//
//            public void onClick(View view) {
//                text = (EditText)findViewById(R.id.writePlace);
//                String string =text.getText().toString();
//
//
//                Intent myIntent = new Intent(Menu.this,Upcoming_Walks.class);
//                myIntent.putExtra("key",string);
//                startActivity(myIntent);
//



//            }
//        });


    }





package com.example.ontime;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

public class Suggestion extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoComplete1);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(Suggestion.this,android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address:", autoCompleteTextView.getText().toString());
            }
        });

    }

//    public LatLng getLatLngFromAddress(String address){
//        Geocoder geocoder = new Geocoder(Suggestion.this);
//        List<Address> addressList;
//
//        try {
//            addressList=geocoder.getFromLocationName(address,1);
//            if (addressList!=null){
//                Address singleAddress = addressList.get(0);
//                LatLng latLng = new LatLng(singleAddress.getLatitude(),singleAddress.getLongitude());
//                return latLng;
//            } else {
//                return null;
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//            return null;
//        }
//
//    }
}

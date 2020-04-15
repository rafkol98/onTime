package com.example.ontime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    GoogleMap map;
    double currentLat;
    double currentLong;
    String destinationPassed;
    public String bestProvider;
    public Criteria criteria;
    public LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        getCurrentLocation();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
    }

    public void getCurrentLocation() {
        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location != null) {
            currentLong = location.getLongitude();
            currentLat = location.getLatitude();
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    //This method is used to build the url string that is sent to the maps api to request directions
    private String getRequestUrl() {
        //Value of origin
        String str_origin2 = "origin=" + currentLat + "," + currentLong;
        String str_origin_as_str = "origin=" + "Newcastle";
        //Value of destination
        String str_destination2 = "destination=" + (currentLat+0.01) + "," + (currentLong+0.01);
        //Set value enable the sensor
        String sensor = "sensor=false";
        //Mode of travel
        String mode= "mode=walking";
        //Build the full string with the variables
        String param = str_origin2 + "&" + str_destination2 + "&" + sensor + "&" + mode;
        //Output format
        String output = "json";
        //Create url to request
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + "AIzaSyAUHI3Ny9kHYrmYQ_c6uXQSwLFWSiyJ4Ko";
        return url;
    }

    //once the map fragment has loaded do this
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        try{
        boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw.b_w_style1));

        if(!success){
            Log.d("MapActivity", "Style parsing failes");
        }
        } catch (Resources.NotFoundException e){
            Log.d("MapActivity", "Can't find style");
        }

        //enable the zoom in and out buttons bottom right
        map.getUiSettings().setZoomControlsEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("key");
        }

        //enables the set view to current location top right
        map.setMyLocationEnabled(true);
        LatLng place = getLatLngFromAddress(destinationPassed);

        float zoom = 14.5f;
        if(place != null) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(place, zoom));
        googleMap.addMarker(new MarkerOptions().position(place)
                    .title(destinationPassed));
            Toast.makeText(Map.this,"Click on the Marker to select the trip",Toast.LENGTH_LONG);
        } else{
            LatLng current = new LatLng(currentLat,currentLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
            Toast.makeText(Map.this,"Location Could Not be Found",Toast.LENGTH_LONG);
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }



    public LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(Map.this);
        List<Address> addressList;

        try {
            addressList = geocoder.getFromLocationName(address, 1);
            if (addressList != null) {
                Address singleAddress = addressList.get(0);
                LatLng latLng = new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude());
                return latLng;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void onPlan(View v){
        //go to select time class after user clicks on plan trip.
        Intent myIntent = new Intent(Map.this, SelectTime.class);
        myIntent.putExtra("keyMap", destinationPassed);
        startActivity(myIntent);



    }

}

package com.example.ontime.MapRelatedClasses;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MainClasses.PlanTripFromLocation;
import com.example.ontime.R;
import com.example.ontime.DateTimeClasses.SelectTime;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;


/**
 * This is the Map class. It is the map that shows the user the location of the place that they
 * selected to go in the Tab1 fragment (the main page of the app-where the user selects where he
 * wants to go).
 */
public class Map extends FragmentActivity implements OnMapReadyCallback,
                                                     LocationListener,
                                                     GeoTask.Geo {

    //Initialise variables.
    GoogleMap map;
    double currentLat;
    double currentLong;
    String destinationPassed, currentOrigin;
    public String bestProvider;
    public Criteria criteria;
    public LocationManager locationManager;
    private String tempAverageSpeed;
    private String averageSpeed;
    private double timeToDest;

    TextView textChange;


    GeoTask geoTask;


    private double distance;

    public double getDistance() {
        return distance;
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    //Get current firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Find the best provider for location.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));

        checkLocationPermission();

        //Initialise the GeoTask class.
        geoTask = new GeoTask(Map.this);

        //get current location.
        getCurrentLocation();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_class);
        textChange = (TextView) findViewById(R.id.textChange);
        textChange.setVisibility(View.INVISIBLE);

        final String uId = currentFirebaseUser.getUid();


        //Get average speed of user from firebase database.
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    tempAverageSpeed = dataSnapshot.child(uId).child("Average Speed").getValue().toString();
                    setAverageSpeed(tempAverageSpeed);
                } catch (NullPointerException e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Get the destinationPassed,( a string that carries the destination) which was selected from Tab1 (The main Fragment).
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("key");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }

    //Get current location of the user.
    public void getCurrentLocation() {
        if(checkLocationPermission()){
       Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
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
        }}
    }

    //Once the map fragment has loaded do this.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        //initialise map.
        map = googleMap;

        //load the custom style of the map.
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.b_w_places));

            if (!success) {
                Log.d("MapActivity", "Style parsing failes");
            }
        } catch (Resources.NotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MapActivity", "Can't find style");
        }

        //enable the zoom in and out buttons bottom right
        map.getUiSettings().setZoomControlsEnabled(true);

        //enables the set view to current location.
        if(checkLocationPermission()) {
            map.setMyLocationEnabled(true);
            LatLng destinationLatLng = getLatLngFromAddress(destinationPassed);

            float zoom = 14.5f;

            //Adds a marker to the target destination.
            if (destinationLatLng != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, zoom));
                googleMap.addMarker(new MarkerOptions().position(destinationLatLng)
                        .title(destinationPassed));
            }
            //if it cannot find the location, it puts a market to current location and it notifies the user.
            else {
                LatLng current = new LatLng(currentLat, currentLong);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
                textChange.setText(" ");
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("We could't find the location. Make sure you are connected to the internet and double check the address you entered.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, final int id) {
                                startActivity(new Intent(Map.this, MPage.class));
                            }
                        });
                final AlertDialog alert = builder.create();
                alert.show();
            }


            //Get address of the current origin.
            currentOrigin = getAddressFromLatLng(currentLat, currentLong);


            //Use Geocoder class to calculate minutes walking from current location.
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentLat + "," + currentLong + "&destinations=" + destinationLatLng.latitude + "," + destinationLatLng.longitude + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";
            Log.d("url string", url);
            geoTask.execute(url);
        }

    }

    //When location of the user is changed update currentLat and currentLong variables.
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

    //Returns Latitude and Longitude of an address.
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
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return null;
        }
    }

    //Gets address from latitude and Longitude.
    public String getAddressFromLatLng(double latitude, double longitude) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d("Address", strReturnedAddress.toString());
            } else {
                Log.d("Address", "No Address returned!");
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            Log.d("Address", "Cannot get Address!");
        }
        return strAdd;
    }


    //When the user clicks plan and everything is ok it sends the trip to the selectTime class. Where the user will be able to select a time and date for the trip.
    @SuppressLint("StaticFieldLeak")
    public void onPlan(View v) throws ExecutionException, InterruptedException {

        final LatLng destinationLatLng = getLatLngFromAddress(destinationPassed);


        //Check if current location is more than 5 km away from destination. Dialog to user.
        if (!inRadius(destinationLatLng)) {
            //Alert the user about the distance.
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You seem to be more than 5km away from destination, do you still wanna proceed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            Intent myIntent = new Intent(Map.this, SelectTime.class);
                            myIntent.putExtra("keyMap", destinationPassed);
                            myIntent.putExtra("keyTimeToDest", timeToDest);
                            myIntent.putExtra("keyTime", textChange.getText().toString());
                            myIntent.putExtra("keyLatitude", destinationLatLng.latitude);
                            myIntent.putExtra("keyLongitude", destinationLatLng.longitude);
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Map.this, MPage.class));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        } else {
            //go to select time class after user clicks on plan trip. if the trip passes all the criteria.
            Intent myIntent = new Intent(Map.this, SelectTime.class);
            myIntent.putExtra("keyMap", destinationPassed);
            myIntent.putExtra("keyTimeToDest", timeToDest);
            myIntent.putExtra("keyTime", textChange.getText().toString());
            myIntent.putExtra("keyLatitude", destinationLatLng.latitude);
            myIntent.putExtra("keyLongitude", destinationLatLng.longitude);
            Log.d("here before intent", " " + timeToDest);

            startActivity(myIntent);
        }
    }

    /**
     * Method checks if current location is more than 5 km away from destination.
     * @param destinationLL coordinates of destination location
     * @return boolean whether location is within the recommended 5km distance
     */
    private boolean inRadius(LatLng destinationLL) {
        float[] results = new float[1];
        Location.distanceBetween(currentLat, currentLong, destinationLL.latitude, destinationLL.longitude, results);
        float distanceInMeters = results[0];
        boolean isWithin5km = distanceInMeters < 5000;
        return isWithin5km;
    }

    //Calculates how many minutes the user needs based on his own unique average walking speed to go there.
    @Override
    public void calculateTimeAndDist(String result) {
        try {
            String[] res = result.split(",");
            Double min = Double.parseDouble(res[0]) / 60;
            Double dist = Double.parseDouble(res[1]) / 1000;

            Double d = Double.valueOf(dist);
            Double s = Double.valueOf(getAverageSpeed());

            timeToDest = ((d / s) * 60);

            textChange.setText(timeToDest + "");
            distance = dist;
            Toast.makeText(Map.this, "You need " + (int) timeToDest + " minutes to go there from your current location", Toast.LENGTH_LONG).show();
            Log.d("Distance here here", distance + "");

            //if destination is more than 50km away he has to select an alternative starting location.
            if (dist > 50) {
                Intent myIntent = new Intent(Map.this, PlanTripFromLocation.class);
                ;
                myIntent.putExtra("keyMap", destinationPassed);
                startActivity(myIntent);
            }
        }catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
    }

    @Override
    public void tripFromLocation() {

    }




    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("PERMISSION")
                        .setMessage("We need to acces your current location")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Map.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
                        locationManager.requestLocationUpdates(bestProvider, 400, 1, this);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }


}



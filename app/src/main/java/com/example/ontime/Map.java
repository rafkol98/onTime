package com.example.ontime;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class Map extends FragmentActivity implements OnMapReadyCallback, LocationListener, GeoTask.Geo {

    GoogleMap map;
    double currentLat;
    double currentLong;
    String destinationPassed, currentOrigin;
    public String bestProvider;
    public Criteria criteria;
    public LocationManager locationManager;
    String durationG, distanceG;
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

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        geoTask = new GeoTask(Map.this);


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

                tempAverageSpeed = dataSnapshot.child(uId).child("Average Speed").getValue().toString();
                setAverageSpeed(tempAverageSpeed);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("key");
        }

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

    //once the map fragment has loaded do this
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.b_w_style1));

            if (!success) {
                Log.d("MapActivity", "Style parsing failes");
            }
        } catch (Resources.NotFoundException e) {
            Log.d("MapActivity", "Can't find style");
        }

        //enable the zoom in and out buttons bottom right
        map.getUiSettings().setZoomControlsEnabled(true);

        //enables the set view to current location top right
        map.setMyLocationEnabled(true);
        LatLng destinationLatLng = getLatLngFromAddress(destinationPassed);

        float zoom = 14.5f;
        if (destinationLatLng != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, zoom));
            googleMap.addMarker(new MarkerOptions().position(destinationLatLng)
                    .title(destinationPassed));

        } else {
            LatLng current = new LatLng(currentLat, currentLong);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
            textChange.setText(" ");
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("We could't find the location. Make sure you are connected to the internet and double check the address you entered.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Map.this, Menu.class));
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();
        }

        currentOrigin = getAddressFromLatLng(currentLat, currentLong);


        //Use Geocoder class to calculate minutes walking from current location.
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentOrigin + "&destinations=" + destinationPassed + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";
        Log.d("url string", url);
        geoTask.execute(url);

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
                Log.d("HERE HEREEE address", strReturnedAddress.toString());
            } else {
                Log.d("HERE HEREEE address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("HERE HEREEE address", "Cannot get Address!");
        }
        return strAdd;
    }


    @SuppressLint("StaticFieldLeak")
    public void onPlan(View v) throws ExecutionException, InterruptedException {

        LatLng destinationLatLng = getLatLngFromAddress(destinationPassed);


        //Check if current location is more than 3 km away from destination. Dialog to user.
        if (inRadius(destinationLatLng) == false) {
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
                            startActivity(myIntent);
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(Map.this, Menu.class));
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
            Log.d("here before intent", " " + timeToDest);

            startActivity(myIntent);
        }
    }

    //Method checks if current location is more than 5 km away from destination.
    private boolean inRadius(LatLng destinationLL) {
        float[] results = new float[1];
        Location.distanceBetween(currentLat, currentLong, destinationLL.latitude, destinationLL.longitude, results);
        float distanceInMeters = results[0];
        boolean isWithin3km = distanceInMeters < 5000;
        return isWithin3km;
    }


    @Override
    public void setDouble(String result) {
        String res[] = result.split(",");
        Double min = Double.parseDouble(res[0]) / 60;
        Double dist = Double.parseDouble(res[1]) / 1000;

        Double d = Double.valueOf(dist);
        Double s = Double.valueOf(getAverageSpeed());

        timeToDest = ((d / s) * 60);

        textChange.setText(timeToDest + "");
        distance = dist;
        Toast.makeText(Map.this, "You need "+ (int)timeToDest+ " minutes to go there from your current location", Toast.LENGTH_LONG).show();
        Log.d("Distance here here",distance+"");

        if (dist > 50) {
            Intent myIntent = new Intent(Map.this, PlanTripFromLocation.class);
//            String temp = destinationPassed;
//            Log.d("here here temp dest",temp+"");
            myIntent.putExtra("keyMap", destinationPassed);
            startActivity(myIntent);
        }

    }

    @Override
    public void tripFromLocation() {

    }


}



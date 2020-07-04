package com.example.ontime.MainClasses.fragments;

import android.Manifest;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ontime.DateTimeClasses.SelectTime;
import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MainClasses.PlanTripFromLocation;
import com.example.ontime.MapRelatedClasses.GeoTask;
import com.example.ontime.MapRelatedClasses.Map;
import com.example.ontime.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
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


/**
 * Tab0 is the fragment that shows the map.
 */
public class Tab0 extends Fragment implements OnMapReadyCallback,
        LocationListener {

    //initialise variables.
    GoogleMap map;
    double currentLat;
    double currentLong;

    public String bestProvider;
    public Criteria criteria;
    private String tempAverageSpeed;
    private String averageSpeed;
    public LocationManager locationManager;
    MarkerOptions origin;
    private double timeToDest;
    String destinationPassed;

    double tripLatitude;
    double tripLongitude;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    Button planBtn;

    /**
     * Required empty public constructor
     */
    public Tab0() {
    }

    public String getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab0, container, false);
        planBtn = rootView.findViewById(R.id.buttonPlanTab0);
        //Get map
        if (getActivity() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapUL);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
        return rootView;


    }

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

        permissionEnabled();
        //get the best providers to find location.
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

        //Get and show current location of the user.
        getCurrentLocation();

    }

    /**
     * This method gets the user's current location.
     */
    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            currentLong = location.getLongitude();
            currentLat = location.getLatitude();
        } else {

            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    /**
     *
     */
    public void permissionEnabled() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION
            );

        }
    }

    /**
     * Once the map fragment has loaded do this
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.b_w_places));

            if (!success) {
                Log.d("MapActivity", "Style parsing failed");
            }
        } catch (Resources.NotFoundException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("MapActivity", "Can't find style");
        }


        //enable the zoom in and out buttons bottom right
        final float zoom = 18.0f;
        //Check if we have permission.
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
            LatLng current = new LatLng(currentLat, currentLong);

            map.getUiSettings().setZoomControlsEnabled(true);
            //set the camera to user's current location.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));

            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(final LatLng latLng) {
                    //Create a new marker.
                    MarkerOptions markerOptions = new MarkerOptions();

                    //Set marker position.
                    markerOptions.position(latLng);

                    //Make the marker draggable.
                    markerOptions.draggable(true);

                    //Set the title for the marker.
                    markerOptions.title("CLICK ON THIS LABEL TO PLAN THE TRIP");

                    //Clear any previous markers.
                    map.clear();

                    //Animate the camera to the point.
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

                    //add the new marker.
                    Marker marker = map.addMarker(markerOptions);

                    marker.showInfoWindow();

                    destinationPassed = getAddressFromLatLng(latLng.latitude,latLng.longitude);
                    System.out.println("Destination heree: "+destinationPassed);

                    tripLatitude = latLng.latitude;
                    tripLongitude = latLng.longitude;


                    map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(final Marker marker) {

                            showDialog();

//                            planBtn.setVisibility(View.VISIBLE);
//
//                            planBtn.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    //Alert the user about the distance.
//                                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                                    builder.setMessage("Would you like to plan a trip there?")
//                                            .setCancelable(false)
//                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                public void onClick(final DialogInterface dialog, final int id) {
//                                                    Intent myIntent = new Intent(getContext(), SelectTime.class);
//                                                    myIntent.putExtra("keyMap", destinationPassed);
//                                                    myIntent.putExtra("keyLatitude", tripLatitude);
//                                                    myIntent.putExtra("keyLongitude", tripLongitude);
//                                                    startActivity(myIntent);
//                                                    getActivity().overridePendingTransition(0,0);
//
//                                                }
//                                            })
//                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                                public void onClick(final DialogInterface dialog, final int id) {
//                                                    marker.remove();
//                                                    planBtn.setVisibility(View.INVISIBLE);
//                                                    Log.d("No button works", "malista");
//                                                }
//                                            });
//                                    final AlertDialog alert = builder.create();
//                                    alert.show();
//                                }
//                            });
                        }
                    });

                }
            });
        }
        //if not, ask for permission.
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }


    }

    /**
     * Get user's location if he moves/walks.
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }

    /**
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {
    }


    //Gets address from latitude and Longitude.
    public String getAddressFromLatLng(double latitude, double longitude) {

        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
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


    public void showDialog(){
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.dialog_design,null);

        Button yesBtn = view.findViewById(R.id.yesBtnDialog);
        Button noBtn = view.findViewById(R.id.noBtnDialog);

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Yes Button", "works");
                Intent myIntent = new Intent(getContext(), SelectTime.class);
                myIntent.putExtra("keyMap", destinationPassed);
                myIntent.putExtra("keyLatitude", tripLatitude);
                myIntent.putExtra("keyLongitude", tripLongitude);
                startActivity(myIntent);
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("No Button", "works");
            }
        });

        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setView(view)
                .create();

        alertDialog.show();

    }


}

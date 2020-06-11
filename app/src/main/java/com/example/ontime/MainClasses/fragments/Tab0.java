package com.example.ontime.MainClasses.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ontime.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


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
    public LocationManager locationManager;
    MarkerOptions origin;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Required empty public constructor
     */
    public Tab0() { }


    /**
     *
     *
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
     *
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
//        if (permissionEnabled()) {
            getCurrentLocation();
//        }

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
    public void permissionEnabled(){
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
        float zoom = 18.0f;
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
        }
        //if not, ask for permission.
        else {
        ActivityCompat.requestPermissions(getActivity(), new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                MY_PERMISSIONS_REQUEST_LOCATION);

        }
    }

    /**
     * Get user's location if he moves/walks.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
    }

    /**
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) { }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) { }



}

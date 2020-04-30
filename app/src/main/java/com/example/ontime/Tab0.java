package com.example.ontime;

import android.Manifest;
import android.annotation.SuppressLint;
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
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 */
public class Tab0 extends Fragment implements OnMapReadyCallback, LocationListener {


    GoogleMap map;
    double currentLat;
    double currentLong;

    public String bestProvider;
    public Criteria criteria;
    public LocationManager locationManager;
    MarkerOptions origin;

    public Tab0() {
        // Required empty public constructor
    }


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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        if(getActivity()!=null) {
////            SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager()
////                    .findFragmentById(R.id.mapUL);
////            if (mapFragment != null) {
////                mapFragment.getMapAsync(this);
////            }
////        }

        getCurrentLocation();
        origin = new MarkerOptions().position(new LatLng(currentLat, currentLong)).title("Current Position").snippet("origin");


    }

    public void getCurrentLocation() {

        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        if (location != null) {
            currentLong = location.getLongitude();
            currentLat = location.getLatitude();
        } else {
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
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
    }

    //once the map fragment has loaded do this
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.b_w_places));

            if (!success) {
                Log.d("MapActivity", "Style parsing failes");
            }
        } catch (Resources.NotFoundException e) {
            Log.d("MapActivity", "Can't find style");
        }

        //enable the zoom in and out buttons bottom right
        float zoom = 18.0f;
        map.setMyLocationEnabled(true);
        LatLng current = new LatLng(currentLat, currentLong);

        map.getUiSettings().setZoomControlsEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
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



}

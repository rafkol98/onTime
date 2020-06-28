package com.example.ontime.MeetingsClasses;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ontime.MeetingsClasses.Plan_Meeting;
import com.example.ontime.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.List;


public class Map_Meet extends Fragment implements OnMapReadyCallback {

    //initialise variables.
    GoogleMap map;


    public String bestProvider;
    public Criteria criteria;
    public LocationManager locationManager;
    MarkerOptions origin;

    String destinationPassed;

    Button confirmBtn;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    /**
     * Required empty public constructor
     */
    public Map_Meet() {
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
        View rootView = inflater.inflate(R.layout.fragment_map__meet, container, false);
        confirmBtn = rootView.findViewById(R.id.confirmButton);
        //Get map
        if (getActivity() != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapMeet);
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

        Bundle bundle = this.getArguments();

        if (bundle != null) {
            destinationPassed = getArguments().getString("keyMeeting");
        }

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //the meeting is confirmed. Put it in a bundle, access it from Plan_Meeting Class.
                bundle.putString("confirmedMeeting", destinationPassed);

                Fragment fragmentPlan = new Plan_Meeting();
                fragmentPlan.setArguments(bundle);

                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, fragmentPlan);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();

            }
        });

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
        float zoom = 16.0f;
        //Check if we have permission.
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);

            LatLng meetingPoint = getLatLngFromAddress(destinationPassed);

            map.getUiSettings().setZoomControlsEnabled(true);
            //set the camera to user's current location.
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(meetingPoint, zoom));
            googleMap.addMarker(new MarkerOptions().position(meetingPoint)
                    .title(destinationPassed));
        }
        //if not, ask for permission.
        else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
    }


    //Returns Latitude and Longitude of an address.
    public LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(getContext());
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




}

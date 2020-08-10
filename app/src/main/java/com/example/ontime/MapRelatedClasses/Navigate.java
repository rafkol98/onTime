package com.example.ontime.MapRelatedClasses;


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
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;
import com.example.ontime.SignIn_UpClasses.CLocation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

/**
 * Navigate class. This class is used to navigate a current user
 */
public class Navigate extends FragmentActivity implements OnMapReadyCallback,
                                                          LocationListener,
                                                          GeoTask.Geo {


    //Initialise variables.
    static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

    private GoogleMap mMap;

    String currentOrigin;
    double latitude, longitude;
    MarkerOptions origin, destination;
    private LocationManager locationManager;
    private double currentLat, currentLong;
    String destinationPassed;
    private String bestProvider, url;
    private Criteria criteria;

    LatLng desLatLgn;
    TextView arrivalTxt,txt;

    /**
     *
     */
    public Navigate() {
    }

    GeoTask geoTask;

    //Used to get the User's speed from firebase databse.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    private String tempAverageSpeed;
    private String averageSpeed;

    //Getters and setters.

    /**
     * Getter method for average speed.
     * @return the average speed in String format
     */
    public String getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * Setter method for average speed
     * @param averageSpeed average speed to set
     */
    public void setAverageSpeed(String averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate);

        arrivalTxt = findViewById(R.id.textArrival);
        txt = findViewById(R.id.speedometer_txt);

        //Get uId of the user.
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

        //Gets location service.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


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

        //Initialise Geotask class.
        geoTask = new GeoTask(Navigate.this);

        //Get destination passed and set the destination Lat/Lng to the one we get when we pass in the destination's address.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationPassed = extras.getString("keyDest");
            desLatLgn = getLatLngFromAddress(destinationPassed);
        }


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        doStuff();
        this.updateSpeed(null);

        getLocation();
        Log.d("Des LatLng LOG", " " + desLatLgn);


        //Setting marker to draw route between these two points
        origin = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Position").snippet("origin");
        if (desLatLgn != null) {
            destination = new MarkerOptions().position(desLatLgn).title("Bellandur").snippet("destination");
            url = getDirectionsUrl(origin.getPosition(), destination.getPosition());
        } else {
            destination = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Current Position").snippet("destination");
            // Getting URL to the Google Directions API
            url = getDirectionsUrl(origin.getPosition(), origin.getPosition());
        }


        // Getting URL to the Google Directions API
        Log.d("here here latlong", latitude + "," + longitude);
        //Get address of user's current location
        currentOrigin = getAddressFromLatLng(latitude, longitude);

        // Start downloading json data from Google Directions API
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);


    }




    /**
     * When the map is ready, do this.
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Load style.
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
        float zoom = 14.5f;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.addMarker(destination);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(origin.getPosition()).
                tilt(55).
                zoom(15).
                bearing(0).
                build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mMap.setPadding(0, 200, 0, 0);

        //Use Geocoder class to calculate minutes walking from current location.
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + latitude + "," + longitude + "&destinations=" + destinationPassed + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyB_Y4NILmgU_Ua-dgqY1AVoD81o9qn0yKY";
        Log.d("url string", url);
        geoTask.execute(url);
    }







    /**
     * Speedometer walk for the user.
     */

    @SuppressLint("MissingPermission")
    private void doStuff() {
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) this);
        }

    }

    /**
     * Update speed of the user.
     * @param location
     */
    private void updateSpeed(CLocation location) {
        double nCurrentSpeed = 0;
        if (location != null) {

            nCurrentSpeed = location.getSpeed();
            Log.d("Location speed", nCurrentSpeed+"");

        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.UK, "%5.1f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(" ", "0");


        txt.setText(strCurrentSpeed + " km/h");


    }

    /**
     * Grant permission.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doStuff();
            } else {
                finish();
            }
        }
    }


    /**
     * When the user moves update his location.
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();

        if (location != null) {
            CLocation myLocation = new CLocation(location, true);
            this.updateSpeed(myLocation);
        }

//        String originUpdate = getAddressFromLatLng(currentLat, currentLong);
//        //Use Geocoder class to calculate minutes walking from current location.
//        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + originUpdate + "&destinations=" + destinationPassed + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";
//        Log.d("url string", url);
//        geoTask.execute(url);
    }

    /**
     *
     * @param provider
     * @param status
     * @param extras
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     *
     * @param provider
     */
    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * Get arrival time based on user's unique speed. This is an abstract method's implementation
     * from Geo interface in GeoTask.
     * @param result passed in is returned from the doInBackground method in GeoTask.
     */
    @Override
    public void calculateTimeAndDist(String result) {
        //Create a calendar instance.
        Calendar date = Calendar.getInstance();
        long t = date.getTimeInMillis();

        String pattern = "HH:mm";

        // Create an instance of SimpleDateFormat used for formatting
        // the string representation of date according to the chosen pattern
        DateFormat df = new SimpleDateFormat(pattern);

        //Split the result returned by the Geotask.
        String res[] = result.split(",");
//        Double min = Double.parseDouble(res[0]) / 60;

        //Get the distance.
        Double dist = Double.parseDouble(res[1]) / 1000;


        //Get time to dest based on user's speed.
        Double d = Double.valueOf(dist);
        Double s = Double.valueOf(getAverageSpeed());
        int timeToDest = (int) ((d / s) * 60);

        //Add timeToDest to current time
        Date afterAddingTimeToDest = new Date(t + (timeToDest * ONE_MINUTE_IN_MILLIS));
        String todayAsString = df.format(afterAddingTimeToDest.getTime());

        //Set arrival txt to estimate arrival time.
        arrivalTxt.setText(todayAsString);

    }

    /**
     *
     * @param origin
     * @param dest
     * @return
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //setting transportation mode
        String mode = "mode=walking";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";


        return url;
    }

    /**
     * A method to download json data from url.
     * @param strUrl
     * @return
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * Get Latitude and Longitude from Address.
     * @param address
     * @return
     */
    public LatLng getLatLngFromAddress(String address) {
        Geocoder geocoder = new Geocoder(Navigate.this);
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

    /**
     * Get Current Location. This can later be changed from read from the database. Once we
     * finalise the get of the location of the user constantly in the background.
     */
    protected void getLocation() {
        if (isLocationEnabled(Navigate.this)) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            criteria = new Criteria();
            bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();

            //You can still do this if you like, you might get lucky:
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
            Location location = locationManager.getLastKnownLocation(bestProvider);
            if (location != null) {
                Log.e("TAG", "GPS is on");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            } else {
                //This is what you need:
                locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
            }
        } else {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
//                    .setCancelable(false)
//                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        public void onClick(final DialogInterface dialog, final int id) {
//                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    })
//                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        public void onClick(final DialogInterface dialog, final int id) {
//                            dialog.cancel();
//                        }
//                    });
//            final AlertDialog alert = builder.create();
//            alert.show();
        }
    }

    /**
     * Get Address from Latitude and Longitude.
     * @param latitude
     * @param longitude
     * @return
     */
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
                Log.d("Address Log", strReturnedAddress.toString());
            } else {
                Log.d("Address Log", "No Address returned!");
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            Log.d("Address Log", "Cannot get Address!");
        }
        return strAdd;
    }

    /**
     * Check if location is enabled.
     * @param context
     * @return
     */
    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /**
     *
     */
    @Override
    public void tripFromLocation() {

    }

    /**
     * Private class to get the data from the instructions.
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        /**
         *
         * @param url
         * @return
         */
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }


    /**
     * A class to parse the JSON format.
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        /**
         *  Parsing the data in non-ui thread
         * @param jsonData
         * @return
         */
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
            return routes;
        }

        /**
         *
         * @param result
         */
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = new ArrayList();
            PolylineOptions lineOptions = new PolylineOptions();

            for (int i = 0; i < result.size(); i++) {

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    try{
                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    } catch (NullPointerException e) {
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            if (points.size() != 0)
                mMap.addPolyline(lineOptions);
        }


    }


}
package com.example.ontime;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
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
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Navigate extends FragmentActivity {

//
//    GoogleMap map;
//    double currentLat;
//    double currentLong;
//    String destinationPassed;
//    public String bestProvider;
//    public Criteria criteria;
//    public LocationManager locationManager;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        criteria = new Criteria();
//        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
//
//        getCurrentLocation();
//
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map_class);
//
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//
//        mapFragment.getMapAsync(this);
//    }
//
//    public void getCurrentLocation() {
//        @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
//        if (location != null) {
//            currentLong = location.getLongitude();
//            currentLat = location.getLatitude();
//        } else {
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
//        }
//    }
//
//
//    //once the map fragment has loaded do this
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
////        Suggestion suggestion= new Suggestion();
//
//        map = googleMap;
//
//        //enable the zoom in and out buttons bottom right
//        map.getUiSettings().setZoomControlsEnabled(true);
//
//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            destinationPassed = extras.getString("key");
//
//        }
//        Log.d("Test HERE HERE ", destinationPassed);
//        //enables the set view to current location top right
//        map.setMyLocationEnabled(true);
//        LatLng current = getLatLngFromAddress(destinationPassed);
////        LatLng current = new LatLng(currentLat,currentLong);
//        //To edit the zoom of the default veiw when it's first loaded edit the value below
//        float zoom = 14.5f;
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, zoom));
//
//        //get the url string for the method
//        String url = getRequestUrl();
//
////        Object x = getEndLocation(url);
//
//
////        //get the directions based on the parameters
////        TaskRequstDirections taskRequstDirections = new TaskRequstDirections();
////        taskRequstDirections.execute(url);
//
//    }
//
//    //This method is used to build the url string that is sent to the maps api to request directions
//    private String getRequestUrl() {
//        //Value of origin
//        String str_origin2 = "origin=" + currentLat + "," + currentLong;
//        String str_origin_as_str = "origin=" + "Newcastle";
//        //Value of destination
//        String str_destination2 = +(currentLat + 0.01) + "," + (currentLong + 0.01);
//        //Set value enable the sensor
//        String sensor = "sensor=false";
//        //Mode of travel
//        String mode = "mode=walking";
//        //Build the full string with the variables
//        String param = str_origin2 + "&" + destinationPassed + "&" + sensor + "&" + mode;
//        //Output format
//        String output = "json";
//        //Create url to request
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + param + "&key=" + "AIzaSyAUHI3Ny9kHYrmYQ_c6uXQSwLFWSiyJ4Ko";
//        return url;
//
//    }

//    private ArrayList<Object> getEndLocation(String reqUrl){
//        ArrayList<Object> arrayList = new ArrayList();
//        HttpURLConnection connection=null;
//
//        StringBuilder jsonResult = new StringBuilder();
//        try {
////            StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
////            sb.append("input=" + input);
////            sb.append("&key=AIzaSyAUHI3Ny9kHYrmYQ_c6uXQSwLFWSiyJ4Ko");
//            URL url = new URL(reqUrl);
//            connection = (HttpURLConnection) url.openConnection();
//            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());
//
//            int read;
//
//            char[] buff = new char[1024];
//            while ((read = inputStreamReader.read(buff)) != -1) {
//                jsonResult.append(buff, 0, read);
//            }
//        }
//            catch (MalformedURLException e) {
//                e.printStackTrace();
//            }
//        catch (IOException e) {
//                e.printStackTrace();
//            }finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//
//
//        try {
//            JSONObject jsonObject = new JSONObject(jsonResult.toString());
//            JSONArray endLocation= jsonObject.getJSONArray("routes");
////            JSONArray legs = endLocation.getJSONArray("legs");
//
//
//            for (int i=0; i < endLocation.length(); i++) {
//
//                Object js= ((JSONObject) ((JSONObject) endLocation.get(i)).get("legs")).get("end_location");
//                arrayList.add(js);
//            }
//
//
//        }
//        catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return arrayList;
//
//
//    }


//    private String requestDirection(String reqUrl) throws IOException {
//        String responseString = "";
//        InputStream inputStream = null;
//        HttpURLConnection httpURLConnection = null;
//        try {
//            URL url = new URL(reqUrl);
//            httpURLConnection = (HttpURLConnection) url.openConnection();
//            httpURLConnection.connect();
//
//            //Get the response result
//            inputStream = httpURLConnection.getInputStream();
//            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//
//            StringBuffer stringBuffer = new StringBuffer();
//            String line = "";
//            while ((line = bufferedReader.readLine()) != null) {
//                stringBuffer.append(line);
//            }
//
//            responseString = stringBuffer.toString();
//            bufferedReader.close();
//            inputStreamReader.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                inputStream.close();
//            }
//            httpURLConnection.disconnect();
//        }
//        return responseString;
//    }

//    @Override
//    public void onLocationChanged(Location location) {
//        currentLat = location.getLatitude();
//        currentLong = location.getLongitude();
//    }
//
//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }
//
//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

//    public class TaskRequstDirections extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... strings) {
//            String responseString = "";
//            try {
//                responseString = requestDirection(strings[0]);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            return responseString;
//        }

//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            //Parse json here
//            TaskParser taskParser = new TaskParser();
//            taskParser.execute(s);
//        }
//    }
//
//    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>>> {
//
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
//            JSONObject jsonObject = null;
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                jsonObject = new JSONObject(strings[0]);
//                DirectionsParser directionsParser = new DirectionsParser();
//                routes = directionsParser.parse(jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
//            //Get list routes and display it into the map
//
//            ArrayList points = null;
//
//            PolylineOptions polylineOptions = null;
//
//            for (List<HashMap<String, String>> path : lists) {
//                points = new ArrayList();
//                polylineOptions = new PolylineOptions();
//
//                for (HashMap<String, String> point : path) {
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lon = Double.parseDouble(point.get("lon"));
//
//                    points.add(new LatLng(lat, lon));
//                }
//
//                polylineOptions.addAll(points);
//                polylineOptions.width(15);
//
//                //IF YOU WANT TO CHANGE THE LINE COLOUR CHANGE THE VALUE BELOW/////////////////////////////////////////////////////////////////////////
//                polylineOptions.color(Color.BLUE);
//                polylineOptions.geodesic(true);
//            }
//
//            if (polylineOptions != null) {
//                map.addPolyline(polylineOptions);
//            } else {
//                Toast.makeText(getApplicationContext(), "Direction not found!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


//    public LatLng getLatLngFromAddress(String address) {
//        Geocoder geocoder = new Geocoder(Navigate.this);
//        List<Address> addressList;
//
//        try {
//            addressList = geocoder.getFromLocationName(address, 1);
//            if (addressList != null) {
//                Address singleAddress = addressList.get(0);
//                LatLng latLng = new LatLng(singleAddress.getLatitude(), singleAddress.getLongitude());
//                return latLng;
//            } else {
//                return null;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

}




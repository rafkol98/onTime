package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


import com.example.ontime.DateTimeClasses.DateTimeCheck;
import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MainClasses.Trip;
import com.example.ontime.MainClasses.TripListAdapter;
import com.example.ontime.MapRelatedClasses.CatchUp;
import com.example.ontime.MapRelatedClasses.GeoTask;
import com.example.ontime.MapRelatedClasses.Navigate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.AdapterView.*;


//THIS IS AN OBSOLETE CLASS, BUT I STILL USE IT FOR SOME TESTS.
public class Upcoming_Walks extends AppCompatActivity {

}
//        implements GeoTask.Geo, LocationListener {
//    String destination;
//    Long timestamp;
//
//    double currentLat;
//    double currentLong;
//
//    public Criteria criteria;
//    public String bestProvider;
//
//    private double timeToDest;
//
//    String destinationPassed, currentOrigin;
//    public LocationManager locationManager;
//
//    TripListAdapter adapter;
//    Trip trip;
//
//    String tempAverageSpeed, averageSpeed;
//
//    TextView textChange;
//
//    public String getAverageSpeed() {
//        return averageSpeed;
//    }
//
//    public void setAverageSpeed(String averageSpeed) {
//        this.averageSpeed = averageSpeed;
//    }
//
//    Button deleteBtn;
//    GeoTask geoTask;
//
//    static final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs
//
//    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
//    final ArrayList<Trip> tripList = new ArrayList<>();
//    final ArrayList<String> keyList = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_upcoming_walks);
//
//
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        criteria = new Criteria();
//        bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true)).toString();
//
//        geoTask = new GeoTask(Upcoming_Walks.this);
//
//        textChange = (TextView) findViewById(R.id.textChangeUW);
//
//        final ListView mListView = (ListView) findViewById(R.id.listView);
//        deleteBtn = findViewById(R.id.buttonDelete);
//
//        //Get uId of the user
//        final String uId = currentFirebaseUser.getUid();
//
//
//        //Get average speed of user from firebase database.
//        dbRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                tempAverageSpeed = dataSnapshot.child(uId).child("Average Speed").getValue().toString();
//                setAverageSpeed(tempAverageSpeed);
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        //try it again tomorrow.
//        dbRef.child(uId).child("trips").orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for (DataSnapshot child : dataSnapshot.getChildren()) {
//                    destination = child.child("destination").getValue().toString();
//                    timestamp = child.child("timestamp").getValue(Long.class);
//
//
//                    trip = new Trip(destination, timestamp);
//                    tripList.add(trip);
//                    keyList.add(child.getKey());
//
//                }
//
//                Collections.sort(tripList);
//                adapter = new TripListAdapter(Upcoming_Walks.this, R.layout.adapter_view, tripList);
//                mListView.setAdapter(adapter);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        //When a user clicks on a trip open map with directions there.
//        mListView.setOnItemClickListener(new OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//
//
//                // Get the selected item text from ListView
//                final Trip selectedItem = (Trip) parent.getItemAtPosition(position);
//
//                //get current location and destination of trip selected.
//                getCurrentLocation();
//                currentOrigin = getAddressFromLatLng(currentLat, currentLong);
//                destinationPassed = selectedItem.getDestination();
//
//                //Use Geocoder class to calculate minutes walking from current location.
//                String url = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + currentOrigin + "&destinations=" + destinationPassed + "&mode=walking&language=fr-FR&avoid=tolls&key=AIzaSyBCv-Rz8niwSqwicymjqs_iKinNNsVBAdQ";
//                Log.d("url string", url);
//                geoTask.execute(url);
//
//
////                Log.d("here here trip time ",minutesWalk+"");
//
//                final Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        //get the minutes to walk to destination from current location.
//                        String minutesWalkStr = textChange.getText().toString();
//                        int minutesWalk = (int) Double.parseDouble(minutesWalkStr);
//
//
//                        Log.d("tell me how much it", minutesWalkStr);
//
//                        DateTimeCheck dateTimeCheck = new DateTimeCheck();
//                        String dateTrip = dateTimeCheck.convertTime(selectedItem.getTimestamp());
//                        Date currentDate = Calendar.getInstance().getTime();
////                        currentDate
//                        Calendar addedM = Calendar.getInstance();
//                        addedM.add(Calendar.MINUTE,minutesWalk);
//
//
////                        Date afterAddingTenMins=new Date(currentDate.getTime() + (10 * ONE_MINUTE_IN_MILLIS));
//                        Date afterAddingTenMins = addedM.getTime();
//                        Log.d("here is the date +10", afterAddingTenMins.toString());
//
//
//                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                        String currentStrDate = dateFormat.format(currentDate);
//
//                        String currentDatePlusMins = dateFormat.format(afterAddingTenMins);
//
//                        int difference = dateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm"), currentStrDate, dateTrip);
//
//                        //NEED TO MAKE TEST, WONT MAKE IT ON TIME.
//                        System.out.println(currentDatePlusMins+" here here");
//
////                        //if difference is less than 2 minutes, start the trip
////                        if (dateTimeCheck.getDateDiff(new SimpleDateFormat("dd/MM/yyyy HH:mm"), currentDatePlusMins, dateTrip) < 2) {
////                            Intent myIntent = new Intent(Upcoming_Walks.this, Navigate.class);
////                            myIntent.putExtra("keyDest", selectedItem.getDestination());
////                            startActivity(myIntent);
////                        }
//                        //currentStrDate+timeToWalk
//                        if (dateTimeCheck.startEarlier(new SimpleDateFormat("dd/MM/yyyy HH:mm"), currentDatePlusMins, dateTrip)) {
//
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(Upcoming_Walks.this);
//                            builder.setMessage("You are about to start the walk earlier than expected, are you sure you want to proceed?")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        public void onClick(final DialogInterface dialog, final int id) {
//
//                                            Intent myIntent = new Intent(Upcoming_Walks.this, Navigate.class);
//                                            myIntent.putExtra("keyDest", selectedItem.getDestination());
//                                            startActivity(myIntent);
//                                        }
//                                    })
//                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        public void onClick(final DialogInterface dialog, final int id) {
//                                            finish();
//                                            overridePendingTransition(0, 0);
//                                            startActivity(getIntent());
//                                            overridePendingTransition(0, 0);
//                                        }
//                                    });
//                            final AlertDialog alert = builder.create();
//                            alert.show();
//                        }
//
//                        //if difference between current date and trip date is less than minutes to walk there AND the difference in time is more than or equal to
//                        //time to walk there/2 then the user can still make the trip but he has to speed up.
//                       if (difference < minutesWalk && difference >= (minutesWalk / 2)) {
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(Upcoming_Walks.this);
//                            builder.setMessage("YOU CANT MAKE IT THERE ON TIME ON YOUR REGULAR SPEED, WE HAVE TO WALK FASTER. Do you want to proceed?")
//                                    .setCancelable(false)
//                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                        public void onClick(final DialogInterface dialog, final int id) {
//                                            Intent myIntent = new Intent(Upcoming_Walks.this, CatchUp.class);
//                                            myIntent.putExtra("keyDest", selectedItem.getDestination());
//                                            startActivity(myIntent);
//                                        }
//                                    })
//                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                        public void onClick(final DialogInterface dialog, final int id) {
//                                            dialog.dismiss();
//                                        }
//                                    });
//                            final AlertDialog alert = builder.create();
//                            alert.show();
//                        } else if(difference < minutesWalk && difference < (minutesWalk / 2) ) {
//                            //and delete trip
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(Upcoming_Walks.this);
//                            builder.setMessage("SORRY, YOU CANT MAKE IT THERE ON TIME")
//                                    .setCancelable(false)
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        public void onClick(final DialogInterface dialog, final int id) {
//
//
//                                            //DELETE TRIP.
//                                            Trip item = adapter.getItem(position);
//                                            dbRef.child(uId).child("trips").child(keyList.get(position)).removeValue();
//
//                                            keyList.remove(position);
//                                            adapter.remove(item);
//
//                                            Intent myIntent = new Intent(Upcoming_Walks.this, MPage.class);
//                                            dialog.dismiss();
//                                            startActivity(myIntent);
//                                        }
//                                    });
//                            final AlertDialog alert = builder.create();
//                            alert.show();
//                        }
//
//
//                    }
//                }, 700);
//
//
//            }
//        });
//
//        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            public boolean onItemLongClick(AdapterView<?> parent, View view,
//                                           final int position, long id) {
//
//
//                //Set long button visible
//                deleteBtn.setVisibility(VISIBLE);
//
//                //on click of delete button
//                deleteBtn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Trip item = adapter.getItem(position);
//
//
//                        dbRef.child(uId).child("trips").child(keyList.get(position)).removeValue();
//
//                        keyList.remove(position);
//                        adapter.remove(item);
////                        adapter.notifyDataSetChanged();
////                        mListView.setAdapter(adapter);
//
//                        deleteBtn.setVisibility(INVISIBLE);
//
//                        //reload activity without animation
//                        finish();
//                        overridePendingTransition(0, 0);
//                        startActivity(getIntent());
//                        overridePendingTransition(0, 0);
//
//
//                    }
//                });
//
//                return true;
//            }
//        });
//
//
//    }
//
//    public String getAddressFromLatLng(double latitude, double longitude) {
//
//        String strAdd = "";
//        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//        try {
//            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
//            if (addresses != null) {
//                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");
//
//                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
//                }
//                strAdd = strReturnedAddress.toString();
//                Log.d("HERE HEREEE address", strReturnedAddress.toString());
//            } else {
//                Log.d("HERE HEREEE address", "No Address returned!");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.d("HERE HEREEE address", "Cannot get Address!");
//        }
//        return strAdd;
//    }
//
//    public void onBackPressed() {
//        Intent myIntent = new Intent(Upcoming_Walks.this, MPage.class);
////add a slide back transition. Maybe slidr
//        startActivity(myIntent);
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
//    @Override
//    public void setDouble(String result) {
//        String res[] = result.split(",");
//        Double min = Double.parseDouble(res[0]) / 60;
//        Double dist = Double.parseDouble(res[1]) / 1000;
//
//        System.out.println("I am here");
//        Double d = Double.valueOf(dist);
//        Double s = Double.valueOf(getAverageSpeed());
//        System.out.println("I progressed here is d= " + d + "here is s= " + s);
//        timeToDest = ((d / s) * 60);
//
//        Log.d("here here test 1", timeToDest + "");
//
//        textChange.setText(timeToDest + "");
//
//    }
//
//    @Override
//    public void tripFromLocation() {
//
//    }
//
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
//}





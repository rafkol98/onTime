package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ontime.restarter.RestartServiceBroadcastReceiver;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MPage extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    //Get firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");


    Intent mServiceIntent;
    private LocationService mYourService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_page);
//
//        //If service is not running, start location service.
//        mYourService = new LocationService();
//        mServiceIntent = new Intent(this, mYourService.getClass());
//        if (!isMyServiceRunning(mYourService.getClass())) {
//            startService(mServiceIntent);
//        }


        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Tab1()).commit();


        final String uId = currentFirebaseUser.getUid();

        //Get the no.of trips put it as a badge.
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long childrenL = dataSnapshot.child(uId).child("trips").getChildrenCount();
                String counter = Long.toString(childrenL);
                if (childrenL != 0){
                    BadgeDrawable badgeDrawable = bottomNav.getBadge(R.id.nav_trips);
                    if (badgeDrawable == null)
                        bottomNav.getOrCreateBadge(R.id.nav_trips).setNumber(Integer.parseInt(counter));
                    }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //to stop location service. call stopLocationService


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(getApplicationContext());
        }
    }

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                Log.i ("isMyServiceRunning?", true+"");
//                return true;
//            }
//        }
//        Log.i ("isMyServiceRunning?", false+"");
//        return false;
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        stopService(mServiceIntent);
//        Log.i("MAINACT", "onDestroy!");
//        super.onDestroy();
//
//    }




//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                startLocationService();
//            }else {
//                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }


    //Bottom Navigation. Switch tabs.
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                switch(item.getItemId()){
                    case R.id.nav_map:
                        selectedFragment = new Tab0();
                        break;
                    case R.id.nav_home:
                        selectedFragment = new Tab1();
                        break;

                        case R.id.nav_trips:
                        selectedFragment = new Tab2();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                return true;
            }
        };
//
//
//
//    //Check if the service is running.
//    private boolean isLocationServiceRunning(){
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        if(activityManager != null){
//            for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
//                if(LocationService.class.getName().equals(service.service.getClassName())){
//                    if(service.foreground){
//                        return true;
//                    }
//                }
//            }
//            return false;
//        }
//        Log.d ("Service status", "Not running");
//   return false; }
//
//   //Override onBackPressed. Disable the ability for the user to go back.
//    public void onBackPressed(){
//
//    }
//
//    //Start getting the location service of the user.
//    private void startLocationService(){
//        if (!isLocationServiceRunning()){
//            Intent intent = new Intent(getApplicationContext(), LocationService.class);
//            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
//            startService(intent);
//            Toast.makeText(this,"Location service started", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void stopLocationService(){
//      if(isLocationServiceRunning()){
//          Intent intent = new Intent(getApplicationContext(), LocationService.class);
//          intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
//          startService(intent);
//          Toast.makeText(this,"Location service stopped", Toast.LENGTH_SHORT).show();
//          Log.d("LOCATION_UPDATE","service was tried to be shut down");
//      }
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        stopService(mServiceIntent);
//        Log.i("MAINACT", "onDestroy!");
//        super.onDestroy();
//
//    }


}

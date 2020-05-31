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
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_page);


        findViewById(R.id.buttonStart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MPage.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);

                } else {
                    startLocationService();
                }
            }
        });


        findViewById(R.id.buttonStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });







        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Tab1()).commit();


        final String uId = currentFirebaseUser.getUid();

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length >0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startLocationService();
            }else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

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

    private boolean isLocationServiceRunning(){
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager != null){
            for (ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
                if(LocationService.class.getName().equals(service.service.getClassName())){
                    if(service.foreground){
                        return true;
                    }
                }
            }
            return false;
        }
   return false; }

    public void onBackPressed(){

    }

    private void startLocationService(){
        if (!isLocationServiceRunning()){
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this,"Location service started", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopLocationService(){
      if(!isLocationServiceRunning()){
          Intent intent = new Intent(getApplicationContext(), LocationService.class);
          intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE);
          startService(intent);
          Toast.makeText(this,"Location service stopped", Toast.LENGTH_SHORT).show();
      }
    }


}

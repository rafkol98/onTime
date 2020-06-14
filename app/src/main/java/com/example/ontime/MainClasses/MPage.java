package com.example.ontime.MainClasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.ontime.R;
import com.example.ontime.MainClasses.fragments.Tab0;
import com.example.ontime.MainClasses.fragments.Tab1;
import com.example.ontime.MainClasses.fragments.Tab2;
import com.example.ontime.RestarterAndServices.ProcessClass;
import com.example.ontime.RestarterAndServices.RestartServiceBroadcastReceiver;
import com.example.ontime.MeetingsClasses.Social;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * MainPage. Used to hold all the fragments.
 */
public class MPage extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    //Get firebase user.
    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");
    //EmailToUid table reference.
    private DatabaseReference dbRefEmail = FirebaseDatabase.getInstance().getReference("/emailToUid");
    HashEmail hashEmail = new HashEmail();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_page);

        //Set bottom navigation bar.
        final BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Tab1()).commit();

        //Get uId of the user from the firebase database.
        final String uId = currentFirebaseUser.getUid();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String hashOfEmail = hashEmail.getHashEmail(email);


       DatabaseReference userRef = dbRef.child(uId);

       DatabaseReference emailReff = dbRefEmail.child(hashOfEmail);

       userRef.child("Email").setValue(email);

       //set email of the user.
       emailReff.setValue(uId);


        Log.d("EMAIL of user",email);

        //Get the no.of trips put it as a badge to the walks part in the bottom navigation.
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Long childrenL = dataSnapshot.child("trips").getChildrenCount();
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

    }

    /**
     * Used for getting the user's location on the background all the time, with the help of some
     * other classes (from the restarter_services package).
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            RestartServiceBroadcastReceiver.scheduleJob(getApplicationContext());
        } else {
            ProcessClass bck = new ProcessClass();
            bck.launchService(getApplicationContext());
        }
    }

    /**
     * Bottom Navigation. Switch tabs.
     */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {

                /**
                 *
                 * @param item
                 * @return
                 */
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

                        case R.id.nav_meetings:
                            selectedFragment = new Social();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    return true;
                }
            };
}

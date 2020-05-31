package com.example.ontime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

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

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem tab0, tab1, tab2;
    public PageAdapter pagerAdapter;

    Button btnCounter;

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_page);

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




    }
//        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
//        tab0 = (TabItem) findViewById(R.id.tab0);
//        tab1 = (TabItem) findViewById(R.id.tab1);
//        tab2 = (TabItem) findViewById(R.id.tab2);
//        viewPager = findViewById(R.id.viewpager);
//
//        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
//        viewPager.setAdapter(pagerAdapter);
//        viewPager.setCurrentItem(1);
//        pagerAdapter.notifyDataSetChanged();
//
//
//        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//                if (tab.getPosition() == 0) {
//                    pagerAdapter.notifyDataSetChanged();
//                } else if (tab.getPosition() == 1) {
//                    pagerAdapter.notifyDataSetChanged();
//                } else if (tab.getPosition() == 2) {
//                    pagerAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
//
//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//    }

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

    public void onBackPressed(){

    }

}

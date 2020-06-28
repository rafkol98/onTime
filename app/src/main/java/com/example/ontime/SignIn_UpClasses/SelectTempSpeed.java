package com.example.ontime.SignIn_UpClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;
import com.example.ontime.SignIn_UpClasses.Countdown;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * This activity allows the user to select what speed they think represents the if they cannot do the test.
 */
public class SelectTempSpeed extends AppCompatActivity {

    //Initialise variables.
    Button slowBtn, quiteSlowBtn, averageBtn, quiteFastBtn, fastBtn;

    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("/profiles");

    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_temp_speed);

        //Assing buttons
        slowBtn = findViewById(R.id.buttonSlow);
        quiteSlowBtn = findViewById(R.id.buttonQuiteSlow);
        averageBtn = findViewById(R.id.buttonAverage);
        quiteFastBtn = findViewById(R.id.buttonQuiteFast);
        fastBtn = findViewById(R.id.buttonFast);

        //Assign 3.5 km/h as the user's average speed value in the firebase database.
        slowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = 3.5;
                //Get uId of the user.
                String uId = currentFirebaseUser.getUid();

                //Get average speed reference.
                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(speed);

                //Go to MPage.
                Intent intent = new Intent(SelectTempSpeed.this, MPage.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();


            }
        });

        //Assign 4 km/h as the user's average speed value in the firebase database.
        quiteSlowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = 4;
                //Get uId of the user.
                String uId = currentFirebaseUser.getUid();

                //Get average speed reference.
                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(speed);

                //Go to MPage.
                Intent intent = new Intent(SelectTempSpeed.this, MPage.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        //Assign 4.5 km/h as the user's average speed value in the firebase database.
        averageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = 4.5;
                //Get uId of the user.
                String uId = currentFirebaseUser.getUid();

                //Get average speed reference.
                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(speed);

                //Go to MPage.
                Intent intent = new Intent(SelectTempSpeed.this, MPage.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });

        //Assign 4.80 km/h as the user's average speed value in the firebase database.
        quiteFastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = 4.80;
                //Get uId of the user.
                String uId = currentFirebaseUser.getUid();

                //Get average speed reference.
                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(speed);

                //Go to MPage.
                Intent intent = new Intent(SelectTempSpeed.this, MPage.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });


        //Assign 5.10 km/h as the user's average speed value in the firebase database.
        fastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double speed = 5.10;
                //Get uId of the user.
                String uId = currentFirebaseUser.getUid();

                //Get average speed reference.
                DatabaseReference childReff = dbRef.child(uId).child("Average Speed");

                childReff.setValue(speed);

                //Go to MPage.
                Intent intent = new Intent(SelectTempSpeed.this, MPage.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();
            }
        });


    }


    @Override
    public void onBackPressed() {

    }
}
package com.example.ontime.utilities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.R;
import com.example.ontime.SignIn_UpClasses.MainActivity;
import com.example.ontime.SignIn_UpClasses.RedoTest;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity {

    Button logOut, redo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        //Get logOut button.
        logOut = findViewById(R.id.buttonLogOut);
        redo = findViewById(R.id.buttonRedo);
        //Log out user.
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SettingsActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
                startActivity( i );
                overridePendingTransition(0,0);
                FirebaseAuth.getInstance().signOut();
                finish();
            }
        });

        redo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SettingsActivity.this, RedoTest.class);
                startActivity( i );
                overridePendingTransition(0,0);
            }
        });
    }
}
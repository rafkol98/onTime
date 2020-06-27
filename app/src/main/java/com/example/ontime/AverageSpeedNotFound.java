package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.SignIn_UpClasses.Countdown;
import com.example.ontime.SignIn_UpClasses.SelectTempSpeed;
import com.example.ontime.SignIn_UpClasses.WelcomeNSelect;

public class AverageSpeedNotFound extends AppCompatActivity {

    //Initialise variables.
    Button letsGoBtn, laterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_average_speed_not_found);

        //Assign Buttons.
        letsGoBtn = findViewById(R.id.outsideGoNotBtn);
        laterBtn = findViewById(R.id.laterNotBtn);

        //When the user clicks letsGo he is taken to the test.
        letsGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AverageSpeedNotFound.this, Countdown.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                finish();

            }
        });

        //When the user clicks on the laterBtn he is taken to select a temporary average speed.
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AverageSpeedNotFound.this, SelectTempSpeed.class);
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
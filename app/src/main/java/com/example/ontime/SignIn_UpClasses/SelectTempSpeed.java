package com.example.ontime.SignIn_UpClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.R;
import com.example.ontime.SignIn_UpClasses.Countdown;

public class SelectTempSpeed extends AppCompatActivity {

    //Initialise variables.
    Button slowBtn, quiteSlowBtn, averageBtn, quiteFastBtn, fastBtn;

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

        //Assign 3.5 as the user's average speed value in the firebase database.
        slowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



            }
        });
//



    }


    @Override
    public void onBackPressed() {

    }
}
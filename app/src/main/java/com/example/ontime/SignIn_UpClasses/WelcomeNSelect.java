package com.example.ontime.SignIn_UpClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.R;

public class WelcomeNSelect extends AppCompatActivity {

    //Initialise variables.
    Button letsGoBtn, laterBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_n_select);

        //Assign Buttons.
        letsGoBtn = findViewById(R.id.outsideGoBtn);
        laterBtn = findViewById(R.id.laterBtn);

        //When the user clicks letsGo he is taken to the test.
        letsGoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeNSelect.this, Countdown.class);
                startActivity(intent);
                finish();

            }
        });

        //When the user clicks on the laterBtn he is taken to select a temporary average speed.
        laterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeNSelect.this, SelectTempSpeed.class);
                startActivity(intent);
                finish();

            }
        });

    }

    @Override
    public void onBackPressed() {

    }
}
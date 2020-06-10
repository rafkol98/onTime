package com.example.ontime.SignIn_UpClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ontime.R;

/**
 * Welcome screen.
 */
public class Welcome extends AppCompatActivity {

    Button start;

    /**
     *
     * @param savedInstanceState
     */
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //On click of the start button, go to the Countdown class.
        start = findViewById(R.id.start_button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Welcome.this, Countdown.class);
                startActivity(intent);
                finish();

            }
        });
    }

    /**
     *
     */
    public void onBackPressed() { }
}

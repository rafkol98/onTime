package com.example.ontime.MainClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ontime.R;

/**
 * Screen showed when the trip has been successfully planned.
 */
public class SuperScreen extends AppCompatActivity {


    private Handler mHandler = new Handler();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_screen);


        //postDelayed method, Causes the Runnable r (in this case Class Superscreen) to be added to
        // the message queue, to be run after the specified amount of time elapses.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SuperScreen.this, MPage.class);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    intent.putExtras(getIntent().getExtras());
                }
                startActivity(intent);
                overridePendingTransition(0,0);

            }
        }, 6000);
    }

    /**
     *
     */
    public void onBackPressed() {

    }

}




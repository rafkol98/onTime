package com.example.ontime.SignIn_UpClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;

/**
 * Class shown for a few seconds after the user has completed his account.
 */
public class Cool extends AppCompatActivity {

    private Handler mHandler = new Handler();

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Cool.this, MPage.class);
                startActivity(intent);
            }
        }, 4000);
    }

    /**
     *
     */
    public void onBackPressed() { }
}

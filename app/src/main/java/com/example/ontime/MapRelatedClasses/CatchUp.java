package com.example.ontime.MapRelatedClasses;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;

/**
 * THIS ACTIVITY IS STILL A WORK IN PROGRESS.
 */
public class CatchUp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catch_up);
    }

    public void onBackPressed(){
        Intent myIntent = new Intent(CatchUp.this, MPage.class);

        startActivity(myIntent);
    }
}

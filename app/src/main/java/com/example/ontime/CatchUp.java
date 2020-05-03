package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

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

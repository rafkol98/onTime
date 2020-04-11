package com.example.ontime;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Upcoming_Walks extends AppCompatActivity {
    String location,date,time;
    TextView locView,dateView,timeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_walks);

        locView = findViewById(R.id.location_txt);
        dateView = findViewById(R.id.date_text);
        timeView = findViewById(R.id.time_txt);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            location = extras.getString("keyMap");
            date = extras.getString("keyDate");
            time = extras.getString("keyTime");

            locView.setVisibility(View.VISIBLE);dateView.setVisibility(View.VISIBLE);timeView.setVisibility(View.VISIBLE);
            locView.setText(location);
            dateView.setText(date);
            timeView.setText(time);

        }



    }

}



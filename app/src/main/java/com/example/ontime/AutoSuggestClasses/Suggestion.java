package com.example.ontime.AutoSuggestClasses;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ontime.R;

public class Suggestion extends AppCompatActivity {
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        final AutoCompleteTextView autoCompleteTextView = findViewById(R.id.autoComplete1);
        autoCompleteTextView.setAdapter(new PlaceAutoSuggestAdapter(Suggestion.this,android.R.layout.simple_list_item_1));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Address:", autoCompleteTextView.getText().toString());
            }
        });

    }

}

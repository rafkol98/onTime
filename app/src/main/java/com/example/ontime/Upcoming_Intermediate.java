package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Upcoming_Intermediate extends AppCompatActivity {

//    private ListView list;
//    private ArrayAdapter<String> adapter;
//    private ArrayList<String> arrayList;
//    SelectTime selectTime;
//
//
//    public void passToUpcoming(String location){
//
//        list = (ListView) findViewById(R.id.lvItems);
//        arrayList = new ArrayList<String>();
//        Log.d("HERE HERE","Here "+location);
//
////        // Adapter: You need three parameters 'the context, id of the layout (it will be where the data is shown),
////        // and the array that contains the data
////        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
////
////        // Here, you set the data in your ListView
////        list.setAdapter(adapter);
////
////        if(selectTime.isFlag()==true){
////            arrayList.add(location);
////            // next thing you have to do is check if your adapter has changed
////            adapter.notifyDataSetChanged();
////        }
//        }
//
//
//
//
////         this line adds the data of your EditText and puts in your array
//
//
////    private Handler mHandler = new Handler();
////    Button btn;
////    Bundle b;
////
////    @Override
////    protected void onCreate(Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_select_time);
////
////        mHandler.postDelayed(new Runnable() {
////            @Override
////            public void run() {
////                Intent intent = new Intent(Upcoming_Intermediate.this, SuperScreen.class);
////                intent.putExtras(getIntent().getExtras());
////                startActivity(intent);
////            }
////        }, 100);
////    }
////
////        public void passToUpcoming() {
////
//////
//////        btn=findViewById(R.id.done_btn);
////
////        b = getIntent().getExtras();
////
//////        btn.setOnClickListener(new View.OnClickListener() {
//////            @Override
//////            public void onClick(View v) {
////        String location = b.getString("keyMap");
////        Log.d("HERE HERE ", "HERE HERE " + location);
//////            }
//////        } );
////
////    }
}



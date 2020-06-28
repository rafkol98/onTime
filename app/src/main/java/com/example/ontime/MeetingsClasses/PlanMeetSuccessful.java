package com.example.ontime.MeetingsClasses;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.MainClasses.fragments.Tab1;
import com.example.ontime.R;
import com.example.ontime.SignIn_UpClasses.Cool;


public class PlanMeetSuccessful extends Fragment {

    private Handler mHandler = new Handler();

    /**
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Show this screen for 2.8 seconds and then take him to the main page.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Fragment newFragment = new Tab1();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();

                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack if needed
                transaction.replace(R.id.fragment_container, newFragment);
                transaction.addToBackStack(null);

                // Commit the transaction
                transaction.commit();
            }
        }, 4000);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_meet_successful, container, false);
    }
}
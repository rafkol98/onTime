package com.example.ontime;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {

    //BroadcastReceiver startService again on the background when application was closed.
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        Log.i("Broadcast Listened", "Service tried to stop");
//        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            intent = new Intent(context, LocationService.class);
////            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
////            context.startForegroundService(intent);
//            context.startForegroundService(new Intent(context, LocationService.class));
//        } else {
////            intent = new Intent(context, LocationService.class);
////            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
////            context.startService(intent);
//            context.startService(new Intent(context, LocationService.class));
//        }
//    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Restarter.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, LocationService.class));
        } else {
            context.startService(new Intent(context, LocationService.class));
        }
    }

}

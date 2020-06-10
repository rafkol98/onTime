package com.example.ontime.RestarterAndServices;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

/**
 * This class is used to launch the service for Build SDK Versions BELOW Lollipop.
 * The service is launched in the MPage class (in the MainClasses Package).
 */
public class ProcessClass {
    public static final String TAG = ProcessClass.class.getSimpleName();
    private static Intent serviceIntent = null;

    /**
     *
     */
    public ProcessClass() {
    }

    /**
     *
     * @param context
     */
    private void setServiceIntent(Context context) {
        if (serviceIntent == null) {
            serviceIntent = new Intent(context, Service.class);
        }
    }

    /**
     * Launching the service
     * @param context
     */
    public void launchService(Context context) {
        if (context == null) {
            return;
        }
        setServiceIntent(context);
        // depending on the version of Android we eitehr launch the simple service (version<O)
        // or we start a foreground service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
        Log.d(TAG, "ProcessMainClass: start service go!!!!");
    }
}

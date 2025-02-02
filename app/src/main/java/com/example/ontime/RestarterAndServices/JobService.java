package com.example.ontime.RestarterAndServices;


import android.app.job.JobParameters;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * This class is used to launch the service for Build SDK Versions ABOVE Lollipop.
 * The service is launched in the MPage class (in the MainClasses Package).
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobService extends android.app.job.JobService {
    private static String TAG= JobService.class.getSimpleName();
    private static RestartServiceBroadcastReceiver restartSensorServiceReceiver;
    private static JobService instance;
    private static JobParameters jobParameters;

    /**
     *
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        ProcessClass bck = new ProcessClass();
        bck.launchService(this);
        registerRestarterReceiver();
        instance= this;
        JobService.jobParameters= jobParameters;

        return false;
    }

    /**
     *
     */
    private void registerRestarterReceiver() {

        // the context can be null if app just installed and this is called from restartsensorservice
        // https://stackoverflow.com/questions/24934260/intentreceiver-components-are-not-allowed-to-register-to-receive-intents-when
        // Final decision: in case it is called from installation of new version (i.e. from manifest, the application is
        // null. So we must use context.registerReceiver. Otherwise this will crash and we try with context.getApplicationContext
        if (restartSensorServiceReceiver == null)
            restartSensorServiceReceiver = new RestartServiceBroadcastReceiver();
        else try{
            unregisterReceiver(restartSensorServiceReceiver);
        } catch (Exception e){
            FirebaseCrashlytics.getInstance().recordException(e);
            // not registered
        }
        // give the time to run
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // we register the  receiver that will restart the background service if it is killed
                // see onDestroy of Service
                IntentFilter filter = new IntentFilter();
                filter.addAction(Constants.RESTART_INTENT);
                try {
                    registerReceiver(restartSensorServiceReceiver, filter);
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                    try {
                        getApplicationContext().registerReceiver(restartSensorServiceReceiver, filter);
                    } catch (Exception ex) {
                        FirebaseCrashlytics.getInstance().recordException(ex);
                    }
                }
            }
        }, 1000);

    }

    /**
     * Called if Android kills the job service
     * @param jobParameters
     * @return
     */
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Stopping job");
        Intent broadcastIntent = new Intent(Constants.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        // give the time to run
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                unregisterReceiver(restartSensorServiceReceiver);
            }
        }, 1000);

        return false;
    }

    /**
     * Called when the tracker is stopped for whatever reason
     * @param context
     */
    public static void stopJob(Context context) {
        if (instance!=null && jobParameters!=null) {
            try{
                instance.unregisterReceiver(restartSensorServiceReceiver);
            } catch (Exception e){
                FirebaseCrashlytics.getInstance().recordException(e);
                // not registered
            }
            Log.i(TAG, "Finishing job");
            instance.jobFinished(jobParameters, true);
        }
    }
}
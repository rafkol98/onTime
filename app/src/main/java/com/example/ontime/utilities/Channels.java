package com.example.ontime.utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;

public class Channels extends Application {
    // Channel ID's
    public static final String WALK_ALERT_CHANNEL = "walk10Alert";
    public static final String WALK_1_ALERT_CHANNEL = "walk1Alert";
    public static final String FOREGROUND_CHANNEL = "service";

    // Walk Group ID and Name
    private String groupID = "WALK_ALERTS_ID";
    private CharSequence groupName = "WALK_ALERTS";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    /**
     * Establish notification channels and order them in their respective groups, if any.
     */
    private void createNotificationChannels() {
        // Determine if anything should be done based on the OS in use
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Obtain an instance of the Notification Manager system service
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            // Establish the Channel Groups
            notificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupID, groupName));

            // Establish the 10 Minute Alert Channel
            NotificationChannel channel1 = new NotificationChannel(WALK_ALERT_CHANNEL,
                    "Receive Walk Alerts 10 Minutes Prior",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Allows you to receive alerts of when to begin walking for planned trips.");

            channel1.setLightColor(Color.RED);
            channel1.setVibrationPattern(new long[] {0,2000});
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel1.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,audioAttributes);

            // Establish the Location Service Channel
            NotificationChannel channel2 = new NotificationChannel(FOREGROUND_CHANNEL,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("Required for efficient location services.");

            // Establish the 1 Minute Walk Alert Channel
            NotificationChannel channel3 = new NotificationChannel(WALK_1_ALERT_CHANNEL,
                                                    "Receive Walk Alerts 1 Minute Prior",
                                                           NotificationManager.IMPORTANCE_HIGH);

            // Set channels to their respective groups
            channel1.setGroup(groupID);
            channel3.setGroup(groupID);

            // Create notification channels
            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
            notificationManager.createNotificationChannel(channel3);
        }
    }
}

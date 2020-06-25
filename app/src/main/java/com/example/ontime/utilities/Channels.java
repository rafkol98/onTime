package com.example.ontime.utilities;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.os.Build;
import android.provider.Settings;

public class Channels extends Application {
    public static final String WALK_ALERT_CHANNEL = "walkAlert";
    public static final String FOREGROUND_CHANNEL = "service";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(WALK_ALERT_CHANNEL,
                    "Receive Walk Alerts",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("Allows you to receive alerts of when to begin walking for planned trips.");

            channel1.setLightColor(Color.RED);
            channel1.setVibrationPattern(new long[] {0,2000});
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel1.setSound(Settings.System.DEFAULT_NOTIFICATION_URI,audioAttributes);

            NotificationChannel channel2 = new NotificationChannel(FOREGROUND_CHANNEL,
                    "Location Service",
                    NotificationManager.IMPORTANCE_LOW);
            channel2.setDescription("Required for efficient location services.");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel1);
            notificationManager.createNotificationChannel(channel2);
        }
    }
}

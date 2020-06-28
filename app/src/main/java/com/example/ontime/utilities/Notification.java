package com.example.ontime.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;

public class Notification {

    private static PendingIntent notificationPendingIntent;

    public static final int DEFAULT_NOTIFICATION_ID = 1;

    /**
     *
     * @param context
     * @param title
     * @param text
     * @param icon
     * @return
     */
    public static android.app.Notification setNotification(Context context, String title, String text,
                                                           int icon, String channel_id) {

        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MPage.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getNotificationBuilder(context, channel_id).setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(notificationPendingIntent)
                    .build();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getNotificationBuilder(context, channel_id)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent)
                    .build();
        } else {
            return getNotificationBuilder(context, channel_id)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_LOW)
                    .build();
        }
    }

    /**
     * Display a notification to the user.
     * @param context - Given application context
     * @param title - Title of the notification to be displayed
     * @param text - Body of the notification to be displayed
     * @param icon - Icon to be displayed on the notification
     */
    public static void showNotification(Context context, String title, String text, int icon,
                                        String channel_id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification = getNotificationBuilder(context, channel_id)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);
        }
    }

    /**
     * Display a notification to the user.
     * @param context - Given application context
     * @param title - Title of the notification to be displayed
     * @param text - Body of the notification to be displayed
     * @param icon - Icon to be displayed on the notification
     */
    public static void showNotification(Context context, String title, String text, int icon,
                                        int id, String channel_id, int priority) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification = getNotificationBuilder(context, channel_id)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(priority)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }

    /**
     * Display a notification to the user.
     * @param context - Given application context
     * @param title - Title of the notification to be displayed
     * @param text - Body of the notification to be displayed
     * @param icon - Icon to be displayed on the notification
     */
    public static void showNotification(Context context, String title, String text, int icon,
                                        int id, String channel_id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification = getNotificationBuilder(context, channel_id)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }

    /**
     * Display a notification to the user.
     * @param context - Given application context
     * @param title - Title of the notification to be displayed
     * @param text - Body of the notification to be displayed
     * @param icon - Icon to be displayed on the notification
     * @param intent - Intent to be activated when user interacts with notification
     */
    public static void showNotification(Context context, String title, String text, int icon,
                                        PendingIntent intent, String channel_id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = getNotificationBuilder(context, channel_id)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(intent)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(DEFAULT_NOTIFICATION_ID, notification);
        }
    }

    /**
     * Display a notification to the user.
     * @param context - Given application context
     * @param title - Title of the notification to be displayed
     * @param text - Body of the notification to be displayed
     * @param icon - Icon to be displayed on the notification
     * @param intent - Intent to be activated when user interacts with notification
     */
    public static void showNotification(Context context, String title, String text, int icon,
                                        int id, PendingIntent intent, String channel_id) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = getNotificationBuilder(context, channel_id)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(intent)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }
    /**
     * Obtain the appropriate Notification Builder for the API of the users' device.
     * @param context - Given application context
     * @return the notification builder modified for the appropriate API
     */
    public static NotificationCompat.Builder getNotificationBuilder(Context context,
                                                                    String channel_id) {
        return (new NotificationCompat.Builder(context, channel_id))
                .setVibrate(new long[]{0, 500, 1000})
                .setLights(Color.RED, 3000, 3000)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
    }
}

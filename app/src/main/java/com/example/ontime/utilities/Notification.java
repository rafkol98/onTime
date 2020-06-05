package com.example.ontime.utilities;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.ontime.MainClasses.MPage;
import com.example.ontime.R;

public class Notification {

    private static PendingIntent notificationPendingIntent;

    public static final int DEFAULT_NOTIFICATION_ID = 1;

    /**
     * This is the method  called to create the Notification
     */
    /*public android.app.Notification setNotification(Context context, String title, String text, int icon) {
        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MPage.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            // notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        android.app.Notification notification;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Permanent Notification";
            //mContext.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_LOW;

            String CHANNEL_ID = context.getString(R.string.CHANNEL_ID);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            //String description = mContext.getString(R.string.notifications_description);
            String description = "I would like to receive travel alerts and notifications for:";
            channel.setDescription(description);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
            notification = notificationBuilder
                    //the log is PNG file format with a transparent background
                    .setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(notificationPendingIntent)
                    .build();

        }
        /**
         * Notification is above Lollipop Versions.
         */
       /* else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification = new NotificationCompat.Builder(context, "channel")
                    .setSmallIcon(icon)
                    .setContentTitle(title)
//                    .setColor(mContext.getResources().getColor(R.color.colorAccent))
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build();
        } else {
            notification = new NotificationCompat.Builder(context, "channel")
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent).build();
        }

        return notification;
    }
*/
    public static android.app.Notification setNotification(Context context, String title, String text, int icon) {

        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MPage.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getNotificationBuilder(context).setSmallIcon(icon)
                    .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                    .setContentTitle(title)
                    .setContentText(text)
                    .setContentIntent(notificationPendingIntent)
                    .build();

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getNotificationBuilder(context)
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(android.app.Notification.PRIORITY_MIN)
                    .setContentIntent(notificationPendingIntent)
                    .build();
        } else {
            return getNotificationBuilder(context)
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
    public static void showNotification(Context context, String title, String text, int icon) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        android.app.Notification notification = getNotificationBuilder(context)
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
     * @param intent - Intent to be activated when user interacts with notification
     */
    public static void showNotification(Context context, String title, String text, int icon, PendingIntent intent) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        android.app.Notification notification = getNotificationBuilder(context)
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
     * Obtain the appropriate Notification Builder for the API of the users' device.
     * @param context - Given application context
     * @return the notification builder modified for the appropriate API
     */
    public static NotificationCompat.Builder getNotificationBuilder(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createDefaultNotificationChannel(context);
        }
        NotificationCompat.Builder builder
                = new NotificationCompat.Builder(context, context.getString(R.string.CHANNEL_ID));
        return builder;
    }

    /**
     * Creates the Notification Channel.
     * Sets the name and description of the Channel and the Channel priority to Low.
     * @param context - Given application context
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private static void createDefaultNotificationChannel(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String appName = context.getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(context.getString(R.string.CHANNEL_ID),
                appName, NotificationManager.IMPORTANCE_DEFAULT);
        channel.setImportance(NotificationManager.IMPORTANCE_LOW);
        String description = "I would like to receive travel alerts and notifications for:";
        channel.setDescription(description);

        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}

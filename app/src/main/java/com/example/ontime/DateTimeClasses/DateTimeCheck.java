package com.example.ontime.DateTimeClasses;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This class has different date and time checks. This will be mainly used for validation.
 */
public class DateTimeCheck {

    /**
     * Returns the difference between an old date and a new date.
     * @param format
     * @param oldDate
     * @param newDate
     * @return
     */
    public static int getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return (int) TimeUnit.MINUTES.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Returns true if currentDate is earlier than the tripDate. (If a user decides to start a trip earlier).
     * @param format
     * @param currentDate
     * @param tripDate
     * @return
     */
    public static boolean startEarlier(SimpleDateFormat format, String currentDate, String tripDate) {
        try {
            return format.parse(currentDate).before(format.parse(tripDate));
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Use this method to know that the user is not gonna be on time. NOT YET DEFINED.
     * @param destination
     * @return
     */
    public static boolean willIBeOnTime(String destination){
        return false;
    }

    /**
     * Convert long into date. Useful for when reading the trip's date from the Firebase Database.
     * @param time
     * @return
     */
    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

    /**
     * Converts date to milliseconds.
     * @param dateIn - Date to convert to milliseconds (ms)
     * @return date in milliseconds (ms)
     * @throws ParseException - Signals that an error has been reached unexpectedly while parsing.
     */
    public Long toMilli(String dateIn) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date date = (Date) formatter.parse(dateIn);
        long output = 0;

        try{
            output = date.getTime() / 1000L;
        } catch (NullPointerException e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }

        return output * 1000;
    }

}

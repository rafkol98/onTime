package com.example.ontime.DateTimeClasses;

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

    //Returns the difference between an old date and a new date.
    public static int getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return (int) TimeUnit.MINUTES.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //Returns true if currentDate is earlier than the tripDate. (If a user decides to start a trip earlier).
    public static boolean startEarlier(SimpleDateFormat format, String currentDate, String tripDate) {
        try {
            return format.parse(currentDate).before(format.parse(tripDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //use this method to know that the user is not gonna be on time. NOT YET DEFINED.
    public static boolean willIBeOnTime(String destination){
        return false;
    }

    //Convert long into date. Useful for when reading the trip's date from the Firebase Database.
    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

}

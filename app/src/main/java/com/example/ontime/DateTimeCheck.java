package com.example.ontime;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeCheck {

//    yyyy-MM-dd HH:mm:ss
    //create current date like this.
//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//    Date date = new Date();

    public static int getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return (int) TimeUnit.MINUTES.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean startEarlier(SimpleDateFormat format, String currentDate, String tripDate) {
        try {
            return format.parse(currentDate).before(format.parse(tripDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //use this method to know that the user is not gonna be on time
    public static boolean willIBeOnTime(String destination){
        return false;
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }

}

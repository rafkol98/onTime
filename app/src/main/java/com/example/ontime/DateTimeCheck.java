package com.example.ontime;

import java.text.DateFormat;
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


}

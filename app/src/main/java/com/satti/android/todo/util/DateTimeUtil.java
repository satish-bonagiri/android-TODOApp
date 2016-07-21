package com.satti.android.todo.util;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {


    public static long getTimeStamp(String dateFormatString){

        long timestamp = -1;
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = null;
        try {
            date = (Date)formatter.parse(dateFormatString);
            long output=date.getTime()/1000L;
            String str=Long.toString(output);
            timestamp = Long.parseLong(str) * 1000;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  timestamp;
    }

    public static String getFormattedDate(long timeStamp) {

        DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date netDate = (new Date(timeStamp));
        return sdf.format(netDate);
    }

}

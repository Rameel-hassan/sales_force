package com.ibrahim.salesforce.utilities;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Rameel Hassan
 * Created 22/12/2023 at 11:55 am
 */
public class DateTimeUtilites {


    public static String getDatetime(String dateString) {
        String dt = "";
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC")); //ignoring utc conversion
            Date value = null;
            try {
                value = formatter.parse(dateString);

            } catch (ParseException e) {
                e.printStackTrace();//"h:mm a dd MMM, yyyy"
            }
            dt = getStringFromDate(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dt;
    }
    public static String getCurrentDateTime() {

        Date todayDate = Calendar.getInstance().getTime();
        String dt = "";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        dt = dateFormatter.format(todayDate);
        return dt;
    }
    public static String getStringFromDate(Date date) {
        String dt = "";
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
        dateFormatter.setTimeZone(TimeZone.getDefault());
        dt = dateFormatter.format(date);
        return dt;
    }
}

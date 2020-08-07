package com.nibou.niboucustomer.utils;

import android.content.Context;

import com.nibou.niboucustomer.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class DateFormatUtil {

    private static String SERVER_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static String getFirstDateOfMonth() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        return getRequiredDateFormat(String.valueOf(c.getTimeInMillis()), "dd.MM.yyyy");
    }


    public static boolean isCardExpired(String expDate) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
            simpleDateFormat.setLenient(false);
            Date expiry = simpleDateFormat.parse(expDate);
            if (expiry.before(new Date())) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getServerMilliSeconds(String serverDateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(SERVER_DATE_FORMAT, Locale.US);
        // simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return String.valueOf(simpleDateFormat.parse(serverDateString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLocalFormatDateString(String dateString, String newformat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat newFormat = new SimpleDateFormat(newformat);
        try {
            return newFormat.format(simpleDateFormat.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static Date getLocalFormatDate(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return (simpleDateFormat.parse(getLocalFormatDateString(dateString, "yyyy-MM-dd HH:mm:ss")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getLocalFormatMillies(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return String.valueOf(simpleDateFormat.parse(dateString).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getLocalMillies() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static String getRequiredDateFormat(String milliSeconds, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return formatter.format(calendar.getTime());
    }

    public static Date getRequiredDate(String milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(milliSeconds));
        return calendar.getTime();
    }


    public static String getRequiredFormatDate(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        try {
            return simpleDateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getRequiredDOBFormat(Date time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return simpleDateFormat.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateTimestamp() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.valueOf(calendar.getTimeInMillis());
    }

    public static String getCurrentDay(Context context) {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return context.getString(R.string.sunday);
            case Calendar.MONDAY:
                return context.getString(R.string.monday);
            case Calendar.TUESDAY:
                return context.getString(R.string.tuesday);
            case Calendar.WEDNESDAY:
                return context.getString(R.string.wednesday);
            case Calendar.THURSDAY:
                return context.getString(R.string.thursday);
            case Calendar.FRIDAY:
                return context.getString(R.string.friday);
            case Calendar.SATURDAY:
                return context.getString(R.string.saturday);
        }
        return "";
    }

    public static int getCurrentDayNumber() {
        switch (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
                return 7;
        }
        return 0;
    }

    public static String getDay(Context context, int dayNumber) {
        switch (dayNumber) {
            case 1:
                return context.getString(R.string.monday);
            case 2:
                return context.getString(R.string.tuesday);
            case 3:
                return context.getString(R.string.wednesday);
            case 4:
                return context.getString(R.string.thursday);
            case 5:
                return context.getString(R.string.friday);
            case 6:
                return context.getString(R.string.saturday);
            case 7:
                return context.getString(R.string.sunday);
        }
        return "";
    }
}

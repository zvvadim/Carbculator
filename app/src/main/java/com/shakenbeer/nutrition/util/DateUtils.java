package com.shakenbeer.nutrition.util;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final DateFormat longDate =
            DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    private static final DateFormat mediumDate =
            DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
    private static final DateFormat shortTime =
            DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    public static String longDate(Date date) {
        return longDate.format(date);
    }

    public static String mediumDate(Date date) {
        return mediumDate.format(date);
    }

    public static String shortTime(Date date) {
        return shortTime.format(date);
    }

    public static boolean sameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }
}

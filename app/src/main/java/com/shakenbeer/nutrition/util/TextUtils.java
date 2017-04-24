package com.shakenbeer.nutrition.util;


import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TextUtils {
    private static final DateFormat longDate =
            DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
    private static final DateFormat shortDate =
            DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    private static final DateFormat shortTime =
            DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());

    public static String longDate(Date date) {
        return longDate.format(date);
    }

    public static String shortDate(Date date) {
        return shortDate.format(date);
    }

    public static String shortTime(Date date) {
        return shortTime.format(date);
    }
}

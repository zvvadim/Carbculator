package com.shakenbeer.nutrition.util;


import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class TextUtils {
    private static final DateFormat longDate =
            DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());

    public static String longDate(Date date) {
        return longDate.format(date);
    }
}

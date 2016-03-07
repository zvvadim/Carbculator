package com.shakenbeer.nutrition.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.DataGetter;
import com.shakenbeer.nutrition.model.Day;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbDayGetter implements DataGetter<Day> {

    @SuppressLint("SimpleDateFormat")
    @Override
    public Day get(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }
        Day day = new Day();

        String date = cursor.getString(cursor.getColumnIndex(DbStorage._ID));
        try {
            day.setDate(new SimpleDateFormat(DbStorage.SQLITE_DATE_FORMAT).parse(date));
        } catch (ParseException e) {
            day.setDate(new Date());
        }
        day.setProtein(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_PROTEIN)));
        day.setCarbs(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_CARBS)));
        day.setFat(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_FAT)));
        day.setKcal(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_KCAL)));

        return day;
    }

}

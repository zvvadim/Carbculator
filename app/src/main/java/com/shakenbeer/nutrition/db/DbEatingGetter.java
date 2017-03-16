package com.shakenbeer.nutrition.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.shakenbeer.nutrition.db.DBContract.EatingTable;
import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.DataGetter;
import com.shakenbeer.nutrition.model.Meal;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbEatingGetter implements DataGetter<Meal> {

    @SuppressLint("SimpleDateFormat")
    @Override
    public Meal get(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }
        
        Meal meal = new Meal();
        
        meal.setId(cursor.getLong(cursor.getColumnIndex(DbStorage._ID)));
        
        
        String date = cursor.getString(cursor.getColumnIndex(EatingTable.COLUMN_DATE));
        
        
        try {
            meal.setDate(new SimpleDateFormat(DbStorage.SQLITE_DATETIME_FORMAT).parse(date));
        } catch (ParseException e) {
            meal.setDate(new Date());
        }
       
        meal.setNumber(cursor.getInt(cursor.getColumnIndex(EatingTable.COLUMN_NUMBER)));
        meal.setProtein(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_PROTEIN)));
        meal.setCarbs(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_CARBS)));
        meal.setFat(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_FAT)));
        meal.setKcal(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_KCAL)));
        
        
        return meal;
    }

}

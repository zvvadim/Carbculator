package com.shakenbeer.nutrition.db;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.database.Cursor;

import com.shakenbeer.nutrition.db.DBContract.EatingTable;
import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.DataGetter;
import com.shakenbeer.nutrition.model.Eating;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbEatingGetter implements DataGetter<Eating> {

    @SuppressLint("SimpleDateFormat")
    @Override
    public Eating get(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }
        
        Eating eating = new Eating();
        
        eating.setId(cursor.getLong(cursor.getColumnIndex(DbStorage._ID)));
        
        
        String date = cursor.getString(cursor.getColumnIndex(EatingTable.COLUMN_DATE));
        
        
        try {
            eating.setDate(new SimpleDateFormat(DbStorage.SQLITE_DATETIME_FORMAT).parse(date));
        } catch (ParseException e) {
            eating.setDate(new Date());
        }
       
        eating.setNumber(cursor.getInt(cursor.getColumnIndex(EatingTable.COLUMN_NUMBER)));
        eating.setProtein(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_PROTEIN)));
        eating.setCarbs(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_CARBS)));
        eating.setFat(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_FAT)));
        eating.setKcal(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_KCAL)));
        
        
        return eating;
    }

}

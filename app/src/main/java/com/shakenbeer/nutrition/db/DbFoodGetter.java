package com.shakenbeer.nutrition.db;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.DataGetter;
import com.shakenbeer.nutrition.model.Food;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbFoodGetter implements DataGetter<Food> {

    @Override
    public Food get(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }
        Food food = new Food();

        food.setId(cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)));
        food.setName(cursor.getString(cursor.getColumnIndex(FoodTable.COLUMN_NAME)));
        food.setProteinPerUnit(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_PROTEIN)));
        food.setCarbsPerUnit(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_CARBS)));
        food.setFatPerUnit(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_FAT)));
        food.setKcalPerUnit(cursor.getFloat(cursor.getColumnIndex(FoodTable.COLUMN_KCAL)));
        food.setUnit(cursor.getInt(cursor.getColumnIndex(FoodTable.COLUMN_UNIT)));
        food.setUnitName(cursor.getString(cursor.getColumnIndex(FoodTable.COLUMN_UNIT_NAME)));

        return food;
    }

}

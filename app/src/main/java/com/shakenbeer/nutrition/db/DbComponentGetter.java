package com.shakenbeer.nutrition.db;

import android.database.Cursor;

import com.shakenbeer.nutrition.db.DBContract.ComponentTable;
import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.DataGetter;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbComponentGetter implements DataGetter<Component> {

    @Override
    public Component get(Cursor cursor) {
        if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
            return null;
        }
        Component component = new Component();
        
        component.setId(cursor.getLong(cursor.getColumnIndex(DbStorage._ID)));
        component.setFoodId(cursor.getLong(cursor.getColumnIndex(ComponentTable.COLUMN_FOOD_ID)));
        component.setGrams(cursor.getInt(cursor.getColumnIndex(ComponentTable.COLUMN_GRAMS)));
        component.setFoodName(cursor.getString(cursor.getColumnIndex(FoodTable.COLUMN_NAME)));
        component.setFoodUnitName(cursor.getString(cursor.getColumnIndex(FoodTable.COLUMN_UNIT_NAME)));
        
        return component;
    }

}

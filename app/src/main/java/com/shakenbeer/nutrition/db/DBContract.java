package com.shakenbeer.nutrition.db;

import android.provider.BaseColumns;

/**
 * @author Sviatoslav Melnychenko
 *
 */
final class DBContract {

    private DBContract() {
    }

    static abstract class FoodTable implements BaseColumns {
        static final String TABLE_NAME = "food";        
        static final String COLUMN_NAME = "food_name";
        static final String COLUMN_PROTEIN = "protein";
        static final String COLUMN_CARBS = "carbs";
        static final String COLUMN_FAT = "fat";
        static final String COLUMN_KCAL = "kcal";
        static final String COLUMN_UNIT = "unit";
        static final String COLUMN_DELETED = "deleted";
        static final String COLUMN_UNIT_NAME = "unit_name";
        static final String FULL_ID = TABLE_NAME + "." + _ID;
    }

    static abstract class EatingTable implements BaseColumns {
        static final String TABLE_NAME = "eating";
        static final String COLUMN_NUMBER = "eating_number";
        static final String COLUMN_DATE = "eating_date";
        static final String FULL_ID = TABLE_NAME + "." + _ID;
    }

    static abstract class ComponentTable implements BaseColumns {
        static final String TABLE_NAME = "component";
        static final String FULL_ID = TABLE_NAME + "." + _ID;
        static final String COLUMN_EATING_ID = "eating_id";
        static final String COLUMN_FOOD_ID = "food_id";
        static final String COLUMN_GRAMS = "grams";
    }
}

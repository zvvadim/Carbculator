package com.shakenbeer.nutrition.db;

import android.provider.BaseColumns;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public final class DBContract {

    private DBContract() {
    }

    public static abstract class FoodTable implements BaseColumns {
        public static final String TABLE_NAME = "food";        
        public static final String COLUMN_NAME = "food_name";
        public static final String COLUMN_PROTEIN = "protein";
        public static final String COLUMN_CARBS = "carbs";
        public static final String COLUMN_FAT = "fat";
        public static final String COLUMN_KCAL = "kcal";
        public static final String COLUMN_UNIT = "unit";
        public static final String COLUMN_DELETED = "deleted";
        public static final String COLUMN_UNIT_NAME = "unit_name";
        public static final String FULL_ID = TABLE_NAME + "." + _ID;
    }

    public static abstract class EatingTable implements BaseColumns {
        public static final String TABLE_NAME = "eating";
        public static final String COLUMN_NUMBER = "eating_number";
        public static final String COLUMN_DATE = "eating_date";
        public static final String FULL_ID = TABLE_NAME + "." + _ID;
    }

    public static abstract class ComponentTable implements BaseColumns {
        public static final String TABLE_NAME = "component";
        public static final String FULL_ID = TABLE_NAME + "." + _ID;
        public static final String COLUMN_EATING_ID = "eating_id";
        public static final String COLUMN_FOOD_ID = "food_id";
        public static final String COLUMN_GRAMS = "grams";
    }
}

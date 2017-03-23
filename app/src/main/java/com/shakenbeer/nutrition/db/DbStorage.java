package com.shakenbeer.nutrition.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.text.format.DateFormat;
import android.util.Log;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.db.DBContract.ComponentTable;
import com.shakenbeer.nutrition.db.DBContract.EatingTable;
import com.shakenbeer.nutrition.db.DBContract.FoodTable;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Storage;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DbStorage extends SQLiteOpenHelper implements Storage {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "nutrition.db";

    public static final String _ID = "_id";
    public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String SQLITE_DATETIME_FORMAT = "yyyy-MM-dd HH:mm";
    
    private Context context;

    private static final String DATE_FROM_EATING_DATE = "date(" + EatingTable.COLUMN_DATE + ")";

    public static final String CREATE_TABLE_FOOD = "CREATE TABLE " + FoodTable.TABLE_NAME + " (" + FoodTable._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + FoodTable.COLUMN_NAME + " TEXT, " + FoodTable.COLUMN_PROTEIN
            + " REAL, " + FoodTable.COLUMN_CARBS + " REAL, " + FoodTable.COLUMN_FAT + " REAL, " + FoodTable.COLUMN_KCAL
            + " REAL, " + FoodTable.COLUMN_UNIT + " INTEGER DEFAULT 100, " + FoodTable.COLUMN_DELETED
            + " INTEGER DEFAULT 0, " + FoodTable.COLUMN_UNIT_NAME + " TEXT)";

    public static final String CREATE_TABLE_EATING = "CREATE TABLE " + EatingTable.TABLE_NAME + " (" + EatingTable._ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + EatingTable.COLUMN_NUMBER + " INTEGER, " + EatingTable.COLUMN_DATE
            + " DATETIME)";

    public static final String CREATE_TABLE_COMPONENT = "CREATE TABLE " + ComponentTable.TABLE_NAME + " ("
            + ComponentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ComponentTable.COLUMN_EATING_ID
            + " INTEGER, " + ComponentTable.COLUMN_FOOD_ID + " INTEGER, " + ComponentTable.COLUMN_GRAMS
            + " INTEGER, FOREIGN KEY(" + ComponentTable.COLUMN_EATING_ID + ") REFERENCES " + EatingTable.TABLE_NAME
            + "(" + EatingTable._ID + "), FOREIGN KEY(" + ComponentTable.COLUMN_FOOD_ID + ") REFERENCES "
            + FoodTable.TABLE_NAME + "(" + FoodTable._ID + "))";

    private static final String JOIN_ALL_TABLES = EatingTable.TABLE_NAME + " LEFT OUTER JOIN "
            + ComponentTable.TABLE_NAME + " ON " + EatingTable.FULL_ID + " = " + ComponentTable.COLUMN_EATING_ID
            + " LEFT OUTER JOIN " + FoodTable.TABLE_NAME + " ON " + FoodTable.FULL_ID + " = "
            + ComponentTable.COLUMN_FOOD_ID;

    private static final String JOIN_COMP_FOOD_TABLES = ComponentTable.TABLE_NAME + " LEFT OUTER JOIN "
            + FoodTable.TABLE_NAME + " ON " + FoodTable.FULL_ID + " = " + ComponentTable.COLUMN_FOOD_ID;

    public DbStorage(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DbStorage", CREATE_TABLE_FOOD);
        Log.i("DbStorage", CREATE_TABLE_EATING);
        Log.i("DbStorage", CREATE_TABLE_COMPONENT);
        db.execSQL(CREATE_TABLE_FOOD);
        db.execSQL(CREATE_TABLE_EATING);
        db.execSQL(CREATE_TABLE_COMPONENT);
        
        String[] initFood = context.getResources().getStringArray(R.array.init_food);
        
        for (int i = 0; i < initFood.length; i++) {
            db.execSQL(initFood[i]);
        } 
    }                                                                                                                                                            

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public Cursor queryDays() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(JOIN_ALL_TABLES);

        String[] columns = new String[] { DATE_FROM_EATING_DATE + " as " + _ID,
                componentAmount(FoodTable.COLUMN_PROTEIN), componentAmount(FoodTable.COLUMN_CARBS),
                componentAmount(FoodTable.COLUMN_FAT), componentAmount(FoodTable.COLUMN_KCAL) };

        String groupBy = DATE_FROM_EATING_DATE;

        String sortOrder = DATE_FROM_EATING_DATE + " desc";

        Cursor cursor = queryBuilder.query(getReadableDatabase(), columns, null, null, groupBy, null, sortOrder);

        return cursor;
    }

    @Override
    public Cursor queryFoods() {
        Cursor cursor = getReadableDatabase().query(FoodTable.TABLE_NAME, null, FoodTable.COLUMN_DELETED + " = 0",
                null, null, null, FoodTable.COLUMN_NAME);
        return cursor;
    }

    @Override
    public Cursor queryFoods(String startWith) {
        String where = FoodTable.COLUMN_DELETED + " = 0 and " + FoodTable.COLUMN_NAME + " LIKE '" + startWith + "%'";
        Cursor cursor = getReadableDatabase().query(FoodTable.TABLE_NAME, null, where,
                null, null, null, FoodTable.COLUMN_NAME);
        return cursor;
    }

    @Override
    public Cursor queryEatings(Date date) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(JOIN_ALL_TABLES);

        String[] columns = new String[] { EatingTable.FULL_ID + " as " + _ID, EatingTable.COLUMN_NUMBER,
                EatingTable.COLUMN_DATE, componentAmount(FoodTable.COLUMN_PROTEIN),
                componentAmount(FoodTable.COLUMN_CARBS), componentAmount(FoodTable.COLUMN_FAT),
                componentAmount(FoodTable.COLUMN_KCAL) };

        String groupBy = EatingTable.FULL_ID + ", " + EatingTable.COLUMN_NUMBER + ", " + EatingTable.COLUMN_DATE;

        String sortOrder = EatingTable.COLUMN_DATE;

        String dateString = DateFormat.format(SQLITE_DATE_FORMAT, date).toString();
        String having = "date(" + EatingTable.COLUMN_DATE + ") = date('" + dateString + "')";

        Cursor cursor = queryBuilder.query(getReadableDatabase(), columns, null, null, groupBy, having, sortOrder);

        return cursor;
    }

    @Override
    public Cursor queryEatings() {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(JOIN_ALL_TABLES);

        String[] columns = new String[] { EatingTable.FULL_ID + " as " + _ID, EatingTable.COLUMN_NUMBER,
                EatingTable.COLUMN_DATE, componentAmount(FoodTable.COLUMN_PROTEIN),
                componentAmount(FoodTable.COLUMN_CARBS), componentAmount(FoodTable.COLUMN_FAT),
                componentAmount(FoodTable.COLUMN_KCAL) };

        String groupBy = EatingTable.FULL_ID + ", " + EatingTable.COLUMN_NUMBER + ", " + EatingTable.COLUMN_DATE;

        String sortOrder = EatingTable.COLUMN_DATE;

        Cursor cursor = queryBuilder.query(getReadableDatabase(), columns, null, null, groupBy, null, sortOrder);

        return cursor;
    }

    @Override
    public Cursor queryComponents(Meal meal) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(JOIN_COMP_FOOD_TABLES);

        String[] columns = new String[] { ComponentTable.FULL_ID + " as " + _ID, ComponentTable.COLUMN_FOOD_ID,
                ComponentTable.COLUMN_GRAMS, FoodTable.COLUMN_NAME, FoodTable.COLUMN_UNIT_NAME };

        String selection = ComponentTable.COLUMN_EATING_ID + " = " + meal.getId();

        Cursor cursor = queryBuilder.query(getReadableDatabase(), columns, selection, null, null, null, null);

        return cursor;
    }

    private String componentAmount(String component) {
        return "sum(" + component + " * " + ComponentTable.COLUMN_GRAMS + " / " + FoodTable.COLUMN_UNIT + ") as "
                + component;
    }

    @Override
    public long insertFood(Food food) {
        ContentValues values = setFoodValues(food);
        return getWritableDatabase().insert(FoodTable.TABLE_NAME, null, values);
    }

    @Override
    public void updateFood(Food food) {
        ContentValues values = setFoodValues(food);
        getWritableDatabase().update(FoodTable.TABLE_NAME, values, BaseColumns._ID + " = " + food.getId(), null);
    }

    @Override
    public void deleteFood(Food food) {
        getWritableDatabase().delete(FoodTable.TABLE_NAME, BaseColumns._ID + " = " + food.getId(), null);
    }

    @Override
    public void updateMarkDeleted(Food food) {
        ContentValues values = new ContentValues();
        values.put(FoodTable.COLUMN_DELETED, 1);
        getWritableDatabase().update(FoodTable.TABLE_NAME, values, BaseColumns._ID + " = " + food.getId(), null);
    }

    @Override
    public long insertEating(Meal meal) {
        ContentValues values = setEatingValues(meal);
        return getWritableDatabase().insert(EatingTable.TABLE_NAME, null, values);
    }

    @Override
    public void updateEating(Meal meal) {
        ContentValues values = setEatingValues(meal);
        getWritableDatabase().update(EatingTable.TABLE_NAME, values, BaseColumns._ID + " = " + meal.getId(), null);
    }

    @Override
    public void deleteEating(Meal meal) {
        getWritableDatabase().delete(EatingTable.TABLE_NAME, BaseColumns._ID + " = " + meal.getId(), null);

    }

    //TODO implement next two methods

    @Override
    public Cursor queryDays(int page, int offset) {
        return null;
    }

    @Override
    public Cursor queryFoods(int page, int offset) {
        return null;
    }

    @Override
    public long insertComponent(Component component, long eatingId) {
        ContentValues values = setComponentsValues(component, eatingId);
        return getWritableDatabase().insert(ComponentTable.TABLE_NAME, null, values);
    }

    @Override
    public void updateComponent(Component component, long eatingId) {
        ContentValues values = setComponentsValues(component, eatingId);
        getWritableDatabase().update(ComponentTable.TABLE_NAME, values, BaseColumns._ID + " = " + component.getId(),
                null);
    }

    @Override
    public void deleteComponent(Component component) {
        getWritableDatabase().delete(ComponentTable.TABLE_NAME, BaseColumns._ID + " = " + component.getId(), null);
    }

    @Override
    public void deleteComponents(Meal meal) {
        getWritableDatabase().delete(ComponentTable.TABLE_NAME,
                ComponentTable.COLUMN_EATING_ID + " = " + meal.getId(), null);
    }

    private ContentValues setFoodValues(Food food) {
        ContentValues values = new ContentValues();
        values.put(FoodTable.COLUMN_NAME, food.getName());
        values.put(FoodTable.COLUMN_PROTEIN, food.getProteinPerUnit());
        values.put(FoodTable.COLUMN_FAT, food.getFatPerUnit());
        values.put(FoodTable.COLUMN_CARBS, food.getCarbsPerUnit());
        values.put(FoodTable.COLUMN_KCAL, food.getKcalPerUnit());
        values.put(FoodTable.COLUMN_UNIT, food.getUnit());
        values.put(FoodTable.COLUMN_UNIT_NAME, food.getUnitName());
        return values;
    }

    @SuppressLint("SimpleDateFormat")
    private ContentValues setEatingValues(Meal meal) {
        ContentValues values = new ContentValues();
        String date = new SimpleDateFormat(DbStorage.SQLITE_DATETIME_FORMAT).format(meal.getDate());
        values.put(EatingTable.COLUMN_DATE, date);
        values.put(EatingTable.COLUMN_NUMBER, meal.getNumber());
        return values;
    }

    private ContentValues setComponentsValues(Component component, long eatingId) {
        ContentValues values = new ContentValues();
        values.put(ComponentTable.COLUMN_EATING_ID, eatingId);
        values.put(ComponentTable.COLUMN_FOOD_ID, component.getFoodId());
        values.put(ComponentTable.COLUMN_GRAMS, component.getGrams());
        return values;
    }

}

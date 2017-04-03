package com.shakenbeer.nutrition.model;

import android.content.Context;
import android.database.Cursor;

import com.shakenbeer.nutrition.db.DbComponentGetter;
import com.shakenbeer.nutrition.db.DbDayGetter;
import com.shakenbeer.nutrition.db.DbEatingGetter;
import com.shakenbeer.nutrition.db.DbFoodGetter;
import com.shakenbeer.nutrition.db.DbStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sviatoslav Melnychenko
 */
public class NutritionLab {

    private Storage storage;

    private volatile static NutritionLab instance;

    private NutritionLab(Context context) {
        storage = new DbStorage(context);
    }

    public static NutritionLab getInstance(Context context) {
        if (instance == null) {
            synchronized (NutritionLab.class) {
                if (instance == null) {
                    instance = new NutritionLab(context);
                }
            }
        }
        return instance;
    }

    public DataCursor<Day> getDayCursor() {
        Cursor cursor = storage.queryDays();
        return new DataCursor<Day>(cursor, new DbDayGetter());
    }

    ;

    public DataCursor<Food> getFoodCursor() {
        Cursor cursor = storage.queryFoods();
        return new DataCursor<Food>(cursor, new DbFoodGetter());
    }

    public DataCursor<Food> getFoodCursor(String startWith) {
        Cursor cursor = storage.queryFoods(startWith);
        return new DataCursor<Food>(cursor, new DbFoodGetter());
    }

    public List<Meal> getMeals(Day day) {
        Cursor cursor = storage.queryEatings(day.getDate());
        DataCursor<Meal> eatingCursor = new DataCursor<Meal>(cursor, new DbEatingGetter());
        List<Meal> list = new ArrayList<Meal>();

        if (eatingCursor.moveToFirst()) {
            do {
                list.add(eatingCursor.get());
            } while (eatingCursor.moveToNext());
        }
        eatingCursor.close();
        return list;
    }

    public List<Meal> getMeals() {
        Cursor cursor = storage.queryEatings();
        DataCursor<Meal> eatingCursor = new DataCursor<Meal>(cursor, new DbEatingGetter());
        List<Meal> list = new ArrayList<Meal>();

        if (eatingCursor.moveToFirst()) {
            do {
                list.add(eatingCursor.get());
            } while (eatingCursor.moveToNext());
        }
        eatingCursor.close();
        return list;
    }

    public List<Component> getComponents(Meal meal) {
        Cursor cursor = storage.queryComponents(meal);
        DataCursor<Component> componentCursor = new DataCursor<Component>(cursor, new DbComponentGetter());
        List<Component> list = new ArrayList<Component>();

        if (componentCursor.moveToFirst()) {
            do {
                list.add(componentCursor.get());
            } while (componentCursor.moveToNext());
        }
        componentCursor.close();
        return list;
    }

    public List<Food> getFoods() {
        Cursor cursor = storage.queryFoods();
        DataCursor<Food> foodCursor = new DataCursor<Food>(cursor, new DbFoodGetter());

        List<Food> list = new ArrayList<Food>();

        if (foodCursor.moveToFirst()) {
            do {
                list.add(foodCursor.get());
            } while (foodCursor.moveToNext());
        }
        foodCursor.close();
        return list;
    }

    public long saveFood(Food food) {
        if (food.getId() < 0) {
            return storage.insertFood(food);
        } else {
            storage.updateFood(food);
            return food.getId();
        }
    }

    public void deleteFood(Food food) {
        storage.updateMarkDeleted(food);
    }

    public long saveEating(Meal meal) {
        if (meal.getId() < 0) {
            return storage.insertEating(meal);
        } else {
            storage.updateEating(meal);
            return meal.getId();
        }

    }

    public void saveComponent(Component component, long eatingId) {
        if (component.getId() < 0) {
            storage.insertComponent(component, eatingId);
        } else {
            storage.updateComponent(component, eatingId);
        }

    }

    public void deleteComponent(Component component) {
        storage.deleteComponent(component);
    }

    public void deleteEating(Meal meal) {
        storage.deleteComponents(meal);
        storage.deleteEating(meal);

    }

    public void deleteDay(Day day) {
        Cursor cursor = storage.queryEatings(day.getDate());
        DataCursor<Meal> eatingCursor = new DataCursor<Meal>(cursor, new DbEatingGetter());
        if (eatingCursor.moveToFirst()) {
            do {
                deleteEating(eatingCursor.get());
            } while (eatingCursor.moveToNext());
        }
        eatingCursor.close();
    }

}

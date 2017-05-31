package com.shakenbeer.nutrition.data;

import android.database.Cursor;

import com.shakenbeer.nutrition.db.DbComponentGetter;
import com.shakenbeer.nutrition.db.DbDayGetter;
import com.shakenbeer.nutrition.db.DbEatingGetter;
import com.shakenbeer.nutrition.db.DbFoodGetter;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.model.Storage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * @author Sviatoslav Melnychenko
 */
@Singleton
public class NutritionLab2 {

    private final Storage storage;

    @Inject
    public NutritionLab2(Storage storage) {
        this.storage = storage;
    }

    public DataCursor<Day> getDayCursor() {
        Cursor cursor = storage.queryDays();
        return new DataCursor<>(cursor, new DbDayGetter());
    }

    public DataCursor<Food> getFoodCursor() {
        Cursor cursor = storage.queryFoods();
        return new DataCursor<>(cursor, new DbFoodGetter());
    }

    public DataCursor<Food> getFoodCursor(String startWith) {
        Cursor cursor = storage.queryFoods(startWith);
        return new DataCursor<>(cursor, new DbFoodGetter());
    }

    public Single<List<Day>> getDaysRx(int page, int offset) {
        return Single.just(getDays(page, offset));
    }

    private List<Day> getDays(int page, int offset) {
        Cursor cursor = storage.queryDays(page, offset);
        DataCursor<Day> dayCursor = new DataCursor<>(cursor, new DbDayGetter());
        List<Day> dayList = new ArrayList<>(cursor.getCount());
        if (dayCursor.moveToFirst()) {
            do {
                dayList.add(dayCursor.get());
            } while (dayCursor.moveToNext());
        }
        return dayList;
    }

    public Single<List<Day>> getAllDaysRx() {
        return Single.just(getAllDays());
    }

    private List<Day> getAllDays() {
        Cursor cursor = storage.queryDays();
        DataCursor<Day> dayCursor = new DataCursor<>(cursor, new DbDayGetter());
        List<Day> dayList = new ArrayList<>(cursor.getCount());
        if (dayCursor.moveToFirst()) {
            do {
                dayList.add(dayCursor.get());
            } while (dayCursor.moveToNext());
        }
        return dayList;
    }

    public Single<List<Meal>> getMealsRx(Day day) {
        return Single.just(getMeals(day));
    }

    private List<Meal> getMeals(Day day) {
        Cursor cursor = storage.queryEatings(day.getDate());
        DataCursor<Meal> eatingCursor = new DataCursor<>(cursor, new DbEatingGetter());
        List<Meal> list = new ArrayList<>();

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
        DataCursor<Meal> eatingCursor = new DataCursor<>(cursor, new DbEatingGetter());
        List<Meal> list = new ArrayList<>();

        if (eatingCursor.moveToFirst()) {
            do {
                list.add(eatingCursor.get());
            } while (eatingCursor.moveToNext());
        }
        eatingCursor.close();
        return list;
    }

    public Single<List<Component>> getComponentsRx(Meal meal) {
        return Single.just(getComponents(meal));
    }

    private List<Component> getComponents(Meal meal) {
        Cursor cursor = storage.queryComponents(meal);
        DataCursor<Component> componentCursor = new DataCursor<>(cursor, new DbComponentGetter());
        List<Component> list = new ArrayList<>();

        if (componentCursor.moveToFirst()) {
            do {
                list.add(componentCursor.get());
            } while (componentCursor.moveToNext());
        }
        componentCursor.close();
        return list;
    }

    public Single<Food> getFoodRx(long foodId) {
        return Single.just(getFood(foodId));
    }

    private Food getFood(long foodId) {
        Cursor cursor = storage.queryFood(foodId);
        DataCursor<Food> foodCursor = new DataCursor<>(cursor, new DbFoodGetter());
        if (foodCursor.moveToFirst()) {
            return foodCursor.get();
        } else {
            return new Food();
        }
    }

    public Single<List<Food>> getFoodsRx(int page, int offset) {
        return Single.just(getFoods(page, offset));
    }

    private List<Food> getFoods(int page, int offset) {
        Cursor cursor = storage.queryFoods(page, offset);
        return getFoods(cursor);
    }

    public List<Food> getFoods() {
        Cursor cursor = storage.queryFoods();
        return getFoods(cursor);
    }

    private List<Food> getFoods(Cursor cursor) {
        DataCursor<Food> foodCursor = new DataCursor<>(cursor, new DbFoodGetter());

        List<Food> list = new ArrayList<>();

        if (foodCursor.moveToFirst()) {
            do {
                list.add(foodCursor.get());
            } while (foodCursor.moveToNext());
        }
        foodCursor.close();
        return list;
    }

    public Single<Long> saveFoodRx(final Food food) {
        return Single.fromCallable(() -> saveFood(food));
    }

    private long saveFood(Food food) {
        if (food.getId() < 0) {
            return storage.insertFood(food);
        } else {
            storage.updateFood(food);
            return food.getId();
        }
    }

    public Completable deleteFoodRx(final Food food) {
        return Completable.fromRunnable(() -> storage.updateMarkDeleted(food));
    }

    public void deleteFood(Food food) {
        storage.updateMarkDeleted(food);
    }

    public Single<Long> saveMealRx(final Meal meal) {
        return Single.fromCallable(() -> saveEating(meal));
    }

    private long saveEating(Meal meal) {
        if (meal.getId() < 0) {
            return storage.insertEating(meal);
        } else {
            storage.updateEating(meal);
            return meal.getId();
        }

    }

    public Single<Long> saveComponentRx(final Component component, final long mealId) {
        return Single.fromCallable(() -> saveComponent(component, mealId));
    }

    public long saveComponent(Component component, long mealId) {
        if (component.getId() < 0) {
            return storage.insertComponent(component, mealId);
        } else {
            storage.updateComponent(component, mealId);
            return component.getId();
        }

    }

    public void deleteComponent(Component component) {
        storage.deleteComponent(component);
    }

    public Completable deleteMealRx(Meal meal) {
        return Completable.fromRunnable(() -> deleteEating(meal));
    }

    private void deleteEating(Meal meal) {
        storage.deleteComponents(meal);
        storage.deleteEating(meal);
    }

    public void deleteDay(Day day) {
        Cursor cursor = storage.queryEatings(day.getDate());
        DataCursor<Meal> eatingCursor = new DataCursor<>(cursor, new DbEatingGetter());
        if (eatingCursor.moveToFirst()) {
            do {
                deleteEating(eatingCursor.get());
            } while (eatingCursor.moveToNext());
        }
        eatingCursor.close();
    }

    public Single<Meal> getMealRx(final long mealId) {
        return Single.fromCallable(() -> {
            Cursor cursor = storage.queryMeal(mealId);
            DataCursor<Meal> eatingCursor = new DataCursor<>(cursor, new DbEatingGetter());
            if (eatingCursor.moveToFirst()) {
                return eatingCursor.get();
            } else {
                return new Meal();
            }
        });
    }
}

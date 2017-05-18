package com.shakenbeer.nutrition.model;

import java.util.Date;
import java.util.List;

import android.database.Cursor;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public interface Storage {

    Cursor queryDays();
    
    Cursor queryEatings(Date date);

    Cursor queryEatings();
    
    Cursor queryFoods();

    Cursor queryFoods(String startWith);
    
    Cursor queryComponents(Meal meal);

    long insertFood(Food food);

    void updateFood(Food food);

    void deleteFood(Food food);

    void updateMarkDeleted(Food food);
    
    long insertEating(Meal meal);

    void updateEating(Meal meal);

    long insertComponent(Component component, long eatingId);  

    void updateComponent(Component component, long eatingId);

    void deleteComponent(Component component);

    void deleteComponents(Meal meal);

    void deleteEating(Meal meal);

    Cursor queryDays(int page, int offset);

    Cursor queryFoods(int page, int offset);

    Cursor queryMeal(long id);
}

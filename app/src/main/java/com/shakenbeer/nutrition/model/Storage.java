package com.shakenbeer.nutrition.model;

import java.util.Date;
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
    
    Cursor queryComponents(Eating eating);

    long insertFood(Food food);

    void updateFood(Food food);

    void deleteFood(Food food);

    void updateMarkDeleted(Food food);
    
    long insertEating(Eating eating);

    void updateEating(Eating eating);

    long insertComponent(Component component, long eatingId);  

    void updateComponent(Component component, long eatingId);

    void deleteComponent(Component component);

    void deleteComponents(Eating eating);

    void deleteEating(Eating eating);

    
}

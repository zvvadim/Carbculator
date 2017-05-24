package com.shakenbeer.nutrition.day;


import com.shakenbeer.nutrition.model.Meal;

interface MealListener {
    void onDelete(int position, Meal meal);
}

package com.shakenbeer.nutrition.foodlist;


import com.shakenbeer.nutrition.model.Food;

public interface FoodListener {
    void onDelete(int position, Food food);
}

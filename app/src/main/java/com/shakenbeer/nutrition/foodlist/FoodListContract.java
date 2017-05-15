package com.shakenbeer.nutrition.foodlist;


import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.List;

public interface FoodListContract {

    interface View extends MvpView {
        void showFoods(List<Food> foods);
        void showFoodUi(Food food);
        void showError(String message);
        void removeFood(int position, Food food);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainFoods();
        abstract void onFoodClick(Food food);
        abstract void onNewFoodClick();
        abstract void onRemoveFood(int position, Food food);
    }
}

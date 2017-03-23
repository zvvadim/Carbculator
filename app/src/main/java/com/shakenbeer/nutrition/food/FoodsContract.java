package com.shakenbeer.nutrition.food;


import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.List;

public interface FoodsContract {

    interface View extends MvpView {
        void showFoods(List<Food> foods);
        void showNewFoodUi();
        void showFoodUi(Food food );
        void removeFood(Food food);
        void showError(String message);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainFoods();
        abstract void onFoodClick(Food food);
        abstract void onNewFoodClick();
    }
}

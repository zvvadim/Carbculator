package com.shakenbeer.nutrition.food;


import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

public interface FoodContract {

    interface View extends MvpView {
        void showFood(Food food);
        void showError(String message);
        void showPreviousUi(long foodId);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void setFood(Food food);
        abstract void onNameChanged(String name);
        abstract void onUnitAmountChanged(int amount);
        abstract void onUnitNameChanged(String unitName);
        abstract void onProteinChanged(float value);
        abstract void onFatChanged(float value);
        abstract void onCarbsChanged(float value);
        abstract void onKcalChanged(float value);
        abstract void onSavePerformed();
    }
}

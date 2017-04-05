package com.shakenbeer.nutrition.meal;


import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.Date;
import java.util.List;

public interface MealContract {

    interface View extends MvpView {
        void showMeal(Meal meal);
        void showComponents(List<Component> components);
        void showNewComponent(Component component);
        void removeComponent(Component component, int index);
        void showError(String message);
        void showPreviousUi();
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainComponents(Meal meal);
        abstract void onAddComponentClick();
        abstract void onRemoveComponentClick(Component component, int index);
        abstract void onSaveClick();
        abstract void onMealTypeSelected(int type);
        abstract void onMealDateSelected(int year, int monthOfYear, int dayOfMonth);
        abstract void onMealTimeSelected(int hour, int minutes);
        abstract void onComponentFoodSelected(Component component, Food food);
        abstract void onComponentAmountChanged(Component component, int amount);

    }
}

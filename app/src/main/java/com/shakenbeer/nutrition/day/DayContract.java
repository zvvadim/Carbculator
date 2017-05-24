package com.shakenbeer.nutrition.day;


import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.List;

public interface DayContract {

    interface View extends MvpView {
        void showDay(Day day);
        void showMeals(List<Meal> meals);
        void showMealUi(Meal meal);
        void showError(String message);
        void showMealRemoved(int position);
        void showPreviousUi();
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainMeals(Day day);
        abstract void onMealClick(Meal meal);
        abstract void onAddMealClick();
        abstract void onMealRemove(Meal meal, int position);
    }
}

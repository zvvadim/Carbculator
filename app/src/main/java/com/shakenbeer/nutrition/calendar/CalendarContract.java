package com.shakenbeer.nutrition.calendar;


import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.List;

public interface CalendarContract {

    interface View extends MvpView {
        void clear();
        void showDays(List<Day> days);
        void showDayUi(Day day);
        void showError(String message);
        void showNewMealUi();
        void showDayUpdated(Day day, int position, boolean existing);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainDays();
        abstract void onDayClick(Day day);
        abstract void onDayUpdated(Day day, List<Day> days);
        abstract void onDayGrow(long mealId, List<Day> days);
    }
}

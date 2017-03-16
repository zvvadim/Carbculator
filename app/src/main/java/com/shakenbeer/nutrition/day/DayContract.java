package com.shakenbeer.nutrition.day;


import com.shakenbeer.nutrition.model.Meal;

import java.util.Date;
import java.util.List;

public class DayContract {

    interface View {
        void showEatings(List<Meal> meals);
        void showEating(long id);
    }

    interface Presenter {
        void obtainEatings(Date date);
        void onEatingClick(Meal meal);
    }
}

package com.shakenbeer.nutrition.day;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

class DayPresenter extends DayContract.Presenter {

    private final NutritionLab2 nutritionLab2;
    private Day day;

    @Inject
    DayPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainMeals(Day day) {
        getMvpView().showDay(day);
        this.day = day;
        nutritionLab2.getMealsRx(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meals -> {
                    getMvpView().showMeals(meals);
                    getMvpView().showDay(dayFromMeals(DayPresenter.this.day, meals));
                }, throwable -> getMvpView().showError(throwable.getLocalizedMessage()));

    }

    private Day dayFromMeals(Day day, List<Meal> meals) {
        Day result = new Day();
        result.setDate(day.getDate());
        float protein = 0;
        float fat = 0;
        float carbs = 0;
        float kcal = 0;
        for (int i = 0; i < meals.size(); i++) {
            Meal meal = meals.get(i);
            protein += meal.getProtein();
            fat += meal.getFat();
            carbs += meal.getCarbs();
            kcal += meal.getKcal();
        }
        result.setProtein(protein);
        result.setFat(fat);
        result.setCarbs(carbs);
        result.setKcal(kcal);
        return result;
    }

    @Override
    void onMealClick(Meal meal) {
        getMvpView().showMealUi(meal);
    }

    @Override
    void onAddMealClick() {
        Calendar target = mergeDateTime();
        getMvpView().showMealUi(new Meal(target.getTime()));
    }

    private Calendar mergeDateTime() {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.setTime(day.getDate());
        target.set(Calendar.HOUR_OF_DAY, current.get(Calendar.HOUR_OF_DAY));
        target.set(Calendar.MINUTE, current.get(Calendar.MINUTE));
        return target;
    }
}

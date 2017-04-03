package com.shakenbeer.nutrition.day;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DayPresenter extends DayContract.Presenter {

    private final NutritionLab2 nutritionLab2;

    @Inject
    public DayPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainMeals(Day day) {
        nutritionLab2.getMealsRx(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Meal>>() {
                    @Override
                    public void accept(@NonNull List<Meal> meals) throws Exception {
                        getMvpView().showMeals(meals);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });

    }

    @Override
    void onMealClick(Meal meal) {
        getMvpView().showMealUi(meal);
    }

    @Override
    void onAddMealClick() {

    }
}

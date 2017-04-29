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
    void obtainMeals(final Day day) {
        //Removing this line causes NPE in binding time
        getMvpView().showDay(day);
        nutritionLab2.getMealsRx(day)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Meal>>() {
                    @Override
                    public void accept(@NonNull List<Meal> meals) throws Exception {
                        getMvpView().showMeals(meals);
                        getMvpView().showDay(dayFromMeals(day, meals));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });

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

    }
}

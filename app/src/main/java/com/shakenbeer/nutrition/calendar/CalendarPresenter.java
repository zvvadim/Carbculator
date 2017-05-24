package com.shakenbeer.nutrition.calendar;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.DateUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

public class CalendarPresenter extends CalendarContract.Presenter {

    private static final int OFFSET = 100;
    private final NutritionLab2 nutritionLab2;
    private int page = 0;
    private boolean everythingIsHere = false;

    @Inject
    public CalendarPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainDays() {
        if (everythingIsHere) return;
        nutritionLab2.getDaysRx(page, OFFSET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::process, throwable ->
                        getMvpView().showError(throwable.getLocalizedMessage()));
    }

    private void process(@NonNull List<Day> days) {
        if (page == 0) {
            getMvpView().clear();
        }
        page++;
        getMvpView().showDays(days);
        if (days.size() < OFFSET) {
            thatsAll();
        }
    }

    private void thatsAll() {
        everythingIsHere = true;
    }

    @Override
    void onDayClick(Day day) {
        getMvpView().showDayUi(day);
    }

    @Override
    void onDayUpdated(Day day, List<Day> days) {
        for (int i = 0; i < days.size(); i++) {
            if (DateUtils.sameDay(days.get(i).getDate(), day.getDate())) {
                getMvpView().showDayUpdated(day, i, true);
                return;
            }
        }
    }

    @Override
    void onDayGrow(long mealId, final List<Day> days) {
        nutritionLab2.getMealRx(mealId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(meal -> processNewMeal(meal, days));
    }

    private void processNewMeal(@NonNull Meal meal, List<Day> days) {
        Date date = meal.getDate();
        for (int i = 0; i < days.size(); i++) {
            Day day = days.get(i);
            if (DateUtils.sameDay(date, day.getDate())) {
                growDay(day, meal);
                getMvpView().showDayUpdated(day, i, true);
                return;
            }
        }
        refresh();
    }

    private void refresh() {
        page = 0;
        everythingIsHere = false;
        obtainDays();
    }

    private void growDay(Day day, Meal meal) {
        float protein = day.getProtein() + meal.getProtein();
        float fat = day.getFat() + meal.getFat();
        float carbs = day.getCarbs() + meal.getCarbs();
        float kcal = day.getKcal() + meal.getKcal();
        day.setProtein(protein);
        day.setFat(fat);
        day.setCarbs(carbs);
        day.setKcal(kcal);
    }


}

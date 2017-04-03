package com.shakenbeer.nutrition.calendar;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Day;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CalendarPresenter extends CalendarContract.Presenter {

    private static final int OFFSET = 100;
    private final NutritionLab2 nutritionLab2;
    private int page = 0;

    @Inject
    public CalendarPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainDays() {
        nutritionLab2.getDaysRx(page, OFFSET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Day>>() {
                    @Override
                    public void accept(@NonNull List<Day> days) throws Exception {
                        page++;
                        getMvpView().showDays(days);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });
    }

    @Override
    void onDayClick(Day day) {
        getMvpView().showDayUi(day);
    }

    @Override
    void onAddMealClick() {

    }
}

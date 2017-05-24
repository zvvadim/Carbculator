package com.shakenbeer.nutrition.stat;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Statistics;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class StatisticsPresenter extends StatisticsContract.Presenter {

    private final NutritionLab2 nutritionLab2;

    public StatisticsPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainStatistics() {
        nutritionLab2.getAllDaysRx()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(days -> {
                    float totalProtein = 0;
                    float totalFat = 0;
                    float totalCarbs = 0;
                    float totalKcal = 0;
                    int totalDays = days.size();
                    for (int i = 0; i < totalDays; i++) {
                        Day day = days.get(i);
                        totalProtein += day.getProtein();
                        totalFat += day.getFat();
                        totalCarbs += day.getCarbs();
                        totalKcal += day.getKcal();
                    }
                    Statistics statistics = new Statistics();
                    statistics.setTotalDays(totalDays);
                    statistics.setTotalProtein(totalProtein);
                    statistics.setTotalCarbs(totalCarbs);
                    statistics.setTotalFat(totalFat);
                    statistics.setTotalKcal(totalKcal);
                    return statistics;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(statistics -> getMvpView().showStatistics(statistics), throwable ->
                        getMvpView().showError(throwable.getLocalizedMessage()));
    }
}

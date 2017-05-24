package com.shakenbeer.nutrition.stat;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;
import com.shakenbeer.nutrition.meal.ComponentListener;
import com.shakenbeer.nutrition.meal.MealContract;
import com.shakenbeer.nutrition.meal.MealPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class StatisticsModule {

    @FeatureScope
    @Provides
    public StatisticsContract.Presenter provideStatisticsPresenter(NutritionLab2 nutritionLab2) {
        return new StatisticsPresenter(nutritionLab2);
    }
}

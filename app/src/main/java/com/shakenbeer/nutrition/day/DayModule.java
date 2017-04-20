package com.shakenbeer.nutrition.day;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
public class DayModule {

    @Provides
    @FeatureScope
    DayContract.Presenter provideDayPresenter(NutritionLab2 nutritionLab2) {
        return new DayPresenter(nutritionLab2);
    }
}

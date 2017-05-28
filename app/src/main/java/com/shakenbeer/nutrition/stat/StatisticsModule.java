package com.shakenbeer.nutrition.stat;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
class StatisticsModule {

    @FeatureScope
    @Provides
    StatisticsContract.Presenter provideStatisticsPresenter(NutritionLab2 nutritionLab2) {
        return new StatisticsPresenter(nutritionLab2);
    }
}

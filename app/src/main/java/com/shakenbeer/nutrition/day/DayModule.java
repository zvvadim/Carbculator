package com.shakenbeer.nutrition.day;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
class DayModule {

    private final MealListener mealListener;

    DayModule(MealListener mealListener) {
        this.mealListener = mealListener;
    }

    @Provides
    @FeatureScope
    MealListener provideMealListener() {
        return mealListener;
    }

    @Provides
    @FeatureScope
    DayContract.Presenter provideDayPresenter(NutritionLab2 nutritionLab2) {
        return new DayPresenter(nutritionLab2);
    }
}

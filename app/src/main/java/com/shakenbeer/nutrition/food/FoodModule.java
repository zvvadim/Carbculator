package com.shakenbeer.nutrition.food;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
public class FoodModule {

    @Provides
    @FeatureScope
    FoodContract.Presenter provideDayPresenter(NutritionLab2 nutritionLab2) {
        return new FoodPresenter(nutritionLab2);
    }
}

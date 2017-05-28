package com.shakenbeer.nutrition.foodlist;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
class FoodListModule {

    private final FoodListener foodListener;

    FoodListModule(FoodListener foodListener) {
        this.foodListener = foodListener;
    }

    @FeatureScope
    @Provides
    FoodListener provideFoodListener() {
        return foodListener;
    }

    @FeatureScope
    @Provides
    FoodListContract.Presenter provideFoodListPresenter(NutritionLab2 nutritionLab2) {
        return new FoodListPresenter(nutritionLab2);
    }
}

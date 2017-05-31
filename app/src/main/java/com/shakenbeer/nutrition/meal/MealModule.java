package com.shakenbeer.nutrition.meal;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
class MealModule {

    private final ComponentListener componentListener;

    MealModule(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    @FeatureScope
    @Provides
    ComponentListener provideComponentDeleteListener() {
        return componentListener;
    }

    @FeatureScope
    @Provides
    MealContract.Presenter provideMealPresenter(NutritionLab2 nutritionLab2) {
        return new MealPresenter(nutritionLab2);
    }
}

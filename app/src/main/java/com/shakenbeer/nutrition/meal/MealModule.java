package com.shakenbeer.nutrition.meal;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
public class MealModule {

    private final ComponentListener componentListener;

    public MealModule(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    @FeatureScope
    @Provides
    public ComponentListener provideComponentDeleteListener() {
        return componentListener;
    }

    @FeatureScope
    @Provides
    public MealContract.Presenter provideMealPresenter(NutritionLab2 nutritionLab2) {
        return new MealPresenter(nutritionLab2);
    }
}

package com.shakenbeer.nutrition.meal;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;
import com.shakenbeer.nutrition.util.ui.ItemListener;

import dagger.Module;
import dagger.Provides;

@Module
public class MealModule {

    private final ItemListener itemListener;

    public MealModule(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @FeatureScope
    @Provides
    public ItemListener provideComponentDeleteListener() {
        return itemListener;
    }

    @FeatureScope
    @Provides
    public MealContract.Presenter provideMealPresenter(NutritionLab2 nutritionLab2) {
        return new MealPresenter(nutritionLab2);
    }
}

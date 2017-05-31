package com.shakenbeer.nutrition.meal;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = MealModule.class)
public interface MealComponent {
    void inject(MealActivity mealActivity);
}

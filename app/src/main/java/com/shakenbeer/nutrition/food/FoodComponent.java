package com.shakenbeer.nutrition.food;

import com.shakenbeer.nutrition.day.DayActivity;
import com.shakenbeer.nutrition.day.DayModule;
import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = FoodModule.class)
public interface FoodComponent {
    void inject(FoodActivity foodActivity);
}

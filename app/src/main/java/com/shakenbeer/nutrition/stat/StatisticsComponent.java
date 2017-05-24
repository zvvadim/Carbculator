package com.shakenbeer.nutrition.stat;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;
import com.shakenbeer.nutrition.meal.MealActivity;
import com.shakenbeer.nutrition.meal.MealModule;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = StatisticsModule.class)
public interface StatisticsComponent {
    void inject(StatisticsView statisticsView);
}

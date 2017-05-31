package com.shakenbeer.nutrition.foodlist;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = FoodListModule.class)
public interface FoodListComponent {
    void inject(FoodListView foodListView);
}

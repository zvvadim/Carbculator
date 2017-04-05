package com.shakenbeer.nutrition.injection;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Storage;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    Storage storage();

    NutritionLab2 nutritionLab2();
}

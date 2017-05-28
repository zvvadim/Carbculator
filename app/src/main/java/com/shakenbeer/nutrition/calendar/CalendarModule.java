package com.shakenbeer.nutrition.calendar;

import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Module;
import dagger.Provides;

@Module
class CalendarModule {

    @Provides
    @FeatureScope
    CalendarContract.Presenter provideCalendarPresenter(NutritionLab2 nutritionLab2) {
        return new CalendarPresenter(nutritionLab2);
    }
}

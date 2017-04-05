package com.shakenbeer.nutrition.calendar;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = CalendarModule.class)
public interface CalendarComponent {
    void inject(CalendarView calendarView);
}

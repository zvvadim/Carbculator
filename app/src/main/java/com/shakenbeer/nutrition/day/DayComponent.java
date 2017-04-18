package com.shakenbeer.nutrition.day;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class)
public interface DayComponent {
    void inject(DayActivity dayActivity);
}

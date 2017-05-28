package com.shakenbeer.nutrition.stat;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.FeatureScope;

import dagger.Component;

@FeatureScope
@Component(dependencies = ApplicationComponent.class, modules = StatisticsModule.class)
interface StatisticsComponent {
    void inject(StatisticsView statisticsView);
}

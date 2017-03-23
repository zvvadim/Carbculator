package com.shakenbeer.nutrition.injection;

import android.app.Application;
import android.content.Context;

import com.shakenbeer.nutrition.db.DbStorage;
import com.shakenbeer.nutrition.model.Storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application application;

    public ApplicationModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    Storage provideStorage(Context context) {
        return new DbStorage(context);
    }

}

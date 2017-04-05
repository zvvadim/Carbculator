package com.shakenbeer.nutrition;

import android.app.Application;
import android.content.Context;

import com.shakenbeer.nutrition.injection.ApplicationComponent;
import com.shakenbeer.nutrition.injection.ApplicationModule;
import com.shakenbeer.nutrition.injection.DaggerApplicationComponent;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class CarbculatorApplication extends Application {

    private ApplicationComponent applicationComponent;

    public ApplicationComponent getComponent() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent.builder()
                    .applicationModule(new ApplicationModule(this))
                    .build();
        }
        return applicationComponent;
    }

    public static CarbculatorApplication get(Context context) {
        return (CarbculatorApplication) context.getApplicationContext();
    }
}

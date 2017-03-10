package com.shakenbeer.nutrition;

import java.util.Locale;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class CarbculatorApplication extends Application {

    public final static String APP_LANG = "app_lang";

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}

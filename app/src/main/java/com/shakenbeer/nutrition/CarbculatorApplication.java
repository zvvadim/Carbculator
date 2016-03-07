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
        changeLocale(getApplicationContext());
    }

    public static void changeLocale(Context c) {
        String lang = PreferenceManager.getDefaultSharedPreferences(c).getString(APP_LANG, null);
        if (lang != null) {
            Resources res = c.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = new Locale(lang);
            res.updateConfiguration(conf, dm);
        }
    }
}

package com.shakenbeer.nutrition.ui;

import android.app.Fragment;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class StatisticsActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new StatisticsFragment();
    }

}

package com.shakenbeer.nutrition.ui;

import android.app.Fragment;

import com.shakenbeer.nutrition.model.Day;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Day day = getIntent().getParcelableExtra(DayFragment.EXTRA_DAY);
        return DayFragment.newInstance(day);
    }

}

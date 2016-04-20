package com.shakenbeer.nutrition.ui;

import android.app.Fragment;
import android.os.Bundle;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayListActivity extends SingleFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    protected Fragment createFragment() {
        return new DayListFragment();
    }

}

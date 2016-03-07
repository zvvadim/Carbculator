package com.shakenbeer.nutrition.ui;

import android.app.Fragment;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new FoodListFragment();
    }

}

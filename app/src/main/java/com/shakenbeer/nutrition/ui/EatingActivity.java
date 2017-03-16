package com.shakenbeer.nutrition.ui;

import android.app.Fragment;

import com.shakenbeer.nutrition.model.Meal;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class EatingActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Meal meal = getIntent().getParcelableExtra(EatingFragment.EXTRA_EATING);
        return EatingFragment.newInstance(meal);
    }    

}

package com.shakenbeer.nutrition.ui;

import com.shakenbeer.nutrition.model.Food;

import android.app.Fragment;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Food food = getIntent().getParcelableExtra(FoodFragment.EXTRA_FOOD);
        return FoodFragment.newInstance(food);
    }

}

package com.shakenbeer.nutrition.ui;

import android.app.Fragment;

import com.shakenbeer.nutrition.model.Eating;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class EatingActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        Eating eating = getIntent().getParcelableExtra(EatingFragment.EXTRA_EATING);
        return EatingFragment.newInstance(eating);
    }    

}

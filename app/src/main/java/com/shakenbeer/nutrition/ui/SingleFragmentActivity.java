package com.shakenbeer.nutrition.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import com.shakenbeer.nutrition.R;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public abstract class SingleFragmentActivity extends Activity {
    
    
    protected abstract Fragment createFragment();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        setContentView(getLayoutResId());
        FragmentManager fm = getFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    protected int getLayoutResId() {
        return R.layout.activity_fragment;
    }   
    
}

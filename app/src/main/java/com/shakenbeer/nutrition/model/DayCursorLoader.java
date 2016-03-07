package com.shakenbeer.nutrition.model;

import android.content.Context;
import android.database.Cursor;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayCursorLoader extends SimpleCursorLoader {

    public DayCursorLoader(Context context) {
        super(context);
    }

    @Override
    public Cursor loadInBackground() {
        return NutritionLab.getInstance(getContext()).getDayCursor();
    }

}

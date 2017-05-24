package com.shakenbeer.nutrition.model;

import android.database.Cursor;

/**
 * @author Sviatoslav Melnychenko
 *
 * @param <T>
 */
public interface DataGetter<T> {
    T get(Cursor cursor);
}

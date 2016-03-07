package com.shakenbeer.nutrition.model;

import android.database.Cursor;

/**
 * @author Sviatoslav Melnychenko
 *
 * @param <T>
 */
public interface DataGetter<T> {
    public T get(Cursor cursor);
}

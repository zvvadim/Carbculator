package com.shakenbeer.nutrition.model;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * @author Sviatoslav Melnychenko
 *
 * @param <D>
 */
public class DataCursor<D> extends CursorWrapper {
    
    private DataGetter<D> dataGetter;

    public DataCursor(Cursor cursor, DataGetter<D> dataGetter) {
        super(cursor);
        this.dataGetter = dataGetter;
    }

    public D get() {
        return dataGetter.get(this);
    }

}
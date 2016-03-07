package com.shakenbeer.nutrition.ui;

import java.text.DateFormat;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Day;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class DayCursorAdapter extends CursorAdapter {
    private DataCursor<Day> dayCursor;

    public DayCursorAdapter(Context context, DataCursor<Day> c) {
        super(context, c, 0);
        dayCursor = c;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_nutrition, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Day day = dayCursor.get();
        
        
        
        TextView dateTextView = (TextView) view.findViewById(R.id.name_text_view);
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, context.getResources().getConfiguration().locale);
        String dateSting = df.format(day.getDate());
        dateTextView.setText(dateSting);

        TextView proteinTextView = (TextView) view.findViewById(R.id.protein_text_view);
        proteinTextView.setText(context.getResources().getString(R.string.protein_colon)
                + String.format("%.1f", day.getProtein()));

        TextView carbsTextView = (TextView) view.findViewById(R.id.carbs_text_view);
        carbsTextView.setText(context.getResources().getString(R.string.carbs_colon)
                + String.format("%.1f", day.getCarbs()));

        TextView fatTextView = (TextView) view.findViewById(R.id.fat_text_view);
        fatTextView.setText(context.getResources().getString(R.string.fat_colon) + String.format("%.1f", day.getFat()));

        TextView kcalTextView = (TextView) view.findViewById(R.id.kcal_text_view);
        kcalTextView.setText(context.getResources().getString(R.string.kcal_colon)
                + String.format("%.1f", day.getKcal()));

        TextView pfcTextView = (TextView) view.findViewById(R.id.pfc_ratio);
        pfcTextView.setText(context.getResources().getString(R.string.pfc_ratio) + day.getPfcRatio());

    }

}

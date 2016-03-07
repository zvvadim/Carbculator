package com.shakenbeer.nutrition.ui;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.DataCursor;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.NutritionLab;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class FoodCursorAdapter extends CursorAdapter {

    DataCursor<Food> foodCursor;

    public FoodCursorAdapter(final Context context, DataCursor<Food> c) {
        super(context, c, 0);
        foodCursor = c;
        setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                foodCursor = NutritionLab.getInstance(context).getFoodCursor(constraint.toString());
                return foodCursor;
            }
        });
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_nutrition, parent, false);
        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        nameTextView.setBackgroundColor(context.getResources().getColor(R.color.food_caption_background));
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Food food = foodCursor.get();

        TextView nameTextView = (TextView) view.findViewById(R.id.name_text_view);
        nameTextView.setText(food.getName() + ", " + food.getUnit() + " " +food.getUnitName());

        TextView proteinTextView = (TextView) view.findViewById(R.id.protein_text_view);
        proteinTextView.setText(context.getResources().getString(R.string.protein_colon)
                + String.format("%.1f", food.getProteinPerUnit()));

        TextView carbsTextView = (TextView) view.findViewById(R.id.carbs_text_view);
        carbsTextView.setText(context.getResources().getString(R.string.carbs_colon)
                + String.format("%.1f", food.getCarbsPerUnit()));

        TextView fatTextView = (TextView) view.findViewById(R.id.fat_text_view);
        fatTextView.setText(context.getResources().getString(R.string.fat_colon)
                + String.format("%.1f", food.getFatPerUnit()));

        TextView kcalTextView = (TextView) view.findViewById(R.id.kcal_text_view);
        kcalTextView.setText(context.getResources().getString(R.string.kcal_colon)
                + String.format("%.1f", food.getKcalPerUnit()));
        
        TextView pfcTextView = (TextView) view.findViewById(R.id.pfc_ratio);
        pfcTextView.setText(context.getResources().getString(R.string.pfc_ratio) + food.getPfcRatio());

    }

}

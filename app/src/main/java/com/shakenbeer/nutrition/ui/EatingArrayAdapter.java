package com.shakenbeer.nutrition.ui;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.model.Eating;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class EatingArrayAdapter extends ArrayAdapter<Eating> {
    private Context context;

    public EatingArrayAdapter(Context context, List<Eating> eatings) {
        super(context, 0, eatings);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.list_item_nutrition, null);
        }

        Eating eating = getItem(position);

        TextView nameTextView = (TextView) convertView.findViewById(R.id.name_text_view);
        nameTextView.setText(context.getResources().getStringArray(R.array.eating_names)[eating.getNumber()]);
        nameTextView.setBackgroundColor(context.getResources().getColor(R.color.eating_caption_background));

        TextView proteinTextView = (TextView) convertView.findViewById(R.id.protein_text_view);
        proteinTextView.setText(context.getResources().getString(R.string.protein_colon)
                + String.format("%.1f", eating.getProtein()));

        TextView carbsTextView = (TextView) convertView.findViewById(R.id.carbs_text_view);
        carbsTextView.setText(context.getResources().getString(R.string.carbs_colon)
                + String.format("%.1f", eating.getCarbs()));

        TextView fatTextView = (TextView) convertView.findViewById(R.id.fat_text_view);
        fatTextView.setText(context.getResources().getString(R.string.fat_colon)
                + String.format("%.1f", eating.getFat()));

        TextView kcalTextView = (TextView) convertView.findViewById(R.id.kcal_text_view);
        kcalTextView.setText(context.getResources().getString(R.string.kcal_colon)
                + String.format("%.1f", eating.getKcal()));
        
        TextView pfcTextView = (TextView) convertView.findViewById(R.id.pfc_ratio);
        pfcTextView.setText(context.getResources().getString(R.string.pfc_ratio) + eating.getPfcRatio());

        return convertView;
    }
}

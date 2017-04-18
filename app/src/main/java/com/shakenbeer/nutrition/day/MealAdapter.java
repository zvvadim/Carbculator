package com.shakenbeer.nutrition.day;

import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ItemMealBinding;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.BindingViewHolder;

import javax.inject.Inject;


public class MealAdapter extends BindingAdapter<Meal> {

    @Inject
    public MealAdapter() {
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemMealBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        ((ItemMealBinding) holder.binding).setMeal(items.get(position));
    }
}

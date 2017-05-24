package com.shakenbeer.nutrition.foodlist;

import android.databinding.ViewDataBinding;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakenbeer.nutrition.databinding.ItemFoodBinding;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.BindingViewHolder;

import javax.inject.Inject;


public class FoodAdapter extends BindingAdapter<Food> {

    private final FoodListener foodListener;

    @Inject
    public FoodAdapter(FoodListener foodListener) {
        this.foodListener = foodListener;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        ItemFoodBinding binding = ItemFoodBinding.inflate(inflater, parent, false);
        binding.setListener(foodListener);
        return binding;
    }

    @Override
    public void onBindViewHolder(final BindingViewHolder holder, int position) {
        ItemFoodBinding binding = (ItemFoodBinding) holder.binding;
        binding.setFood(items.get(position));
        binding.deleteFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int adapterPosition = holder.getAdapterPosition();
                foodListener.onDelete(adapterPosition, items.get(adapterPosition));
            }
        });
    }
}

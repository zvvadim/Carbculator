package com.shakenbeer.nutrition.calendar;

import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakenbeer.nutrition.databinding.ItemDayBinding;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.BindingViewHolder;

import javax.inject.Inject;


public class DayAdapter extends BindingAdapter<Day> {

    @Inject
    public DayAdapter() {
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemDayBinding.inflate(inflater, parent, false);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        ((ItemDayBinding) holder.binding).setDay(items.get(position));
    }
}

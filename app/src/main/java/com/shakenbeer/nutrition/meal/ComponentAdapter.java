package com.shakenbeer.nutrition.meal;

import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.shakenbeer.nutrition.databinding.ItemComponentBinding;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.BindingViewHolder;

import javax.inject.Inject;


public class ComponentAdapter extends BindingAdapter<Component> {

    private final ComponentListener deleteListener;

    @Inject
    public ComponentAdapter(ComponentListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        ItemComponentBinding binding = ItemComponentBinding.inflate(inflater, parent, false);
        binding.setListener(deleteListener);
        return binding;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        ItemComponentBinding binding = (ItemComponentBinding) holder.binding;
        binding.setComponent(items.get(position));
        binding.setPosition(position);
    }
}

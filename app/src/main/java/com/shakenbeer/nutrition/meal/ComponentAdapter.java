package com.shakenbeer.nutrition.meal;

import android.databinding.ViewDataBinding;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shakenbeer.nutrition.databinding.ItemComponentBinding;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.BindingViewHolder;
import com.shakenbeer.nutrition.util.ui.SimpleTextWatcher;

import javax.inject.Inject;


public class ComponentAdapter extends BindingAdapter<Component> {

    private final ComponentListener componentListener;

    @Inject
    public ComponentAdapter(ComponentListener componentListener) {
        this.componentListener = componentListener;
    }

    @Override
    protected ViewDataBinding bind(LayoutInflater inflater, ViewGroup parent, int viewType) {
        ItemComponentBinding binding = ItemComponentBinding.inflate(inflater, parent, false);
        binding.setListener(componentListener);
        return binding;
    }

    @Override
    public void onBindViewHolder(final BindingViewHolder holder, int position) {
        ItemComponentBinding binding = (ItemComponentBinding) holder.binding;
        binding.setComponent(items.get(position));
        binding.deleteComponent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentListener.onDelete(holder.getAdapterPosition());
            }
        });
        binding.componentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                componentListener.onSelectFood(holder.getAdapterPosition());
            }
        });
        binding.componentGramsEditText.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                componentListener.onAmountChanged(holder.getAdapterPosition(), s.toString());
            }
        });
    }
}

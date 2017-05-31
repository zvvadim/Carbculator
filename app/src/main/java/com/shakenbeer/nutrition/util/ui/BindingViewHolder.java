package com.shakenbeer.nutrition.util.ui;

import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BindingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public final ViewDataBinding binding;
    private final ClickListener clickListener;

    BindingViewHolder(ViewDataBinding binding, @NonNull ClickListener clickListener) {
        super(binding.getRoot());
        binding.getRoot().setOnClickListener(this);
        this.binding = binding;
        this.clickListener = clickListener;
    }

    @Override
    public void onClick(View v) {
        clickListener.onHolderViewClick(getAdapterPosition(), sharedViews());
    }

    //override in case want to use shared views in transition
    @SuppressWarnings("WeakerAccess")
    public View[] sharedViews() {
        return new View[]{};
    }

    interface ClickListener {
        void onHolderViewClick(int position, View... shared);
    }
}
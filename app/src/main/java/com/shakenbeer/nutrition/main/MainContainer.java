package com.shakenbeer.nutrition.main;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


public class MainContainer extends FrameLayout {

    public MainContainer(@NonNull Context context) {
        super(context);
    }

    public MainContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MainContainer(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void replace(View view) {
        if (getChildAt(0) != null) {
            removeViewAt(0);
        }
        addView(view);
    }
}

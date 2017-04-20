package com.shakenbeer.nutrition.meal;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityMealBinding;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.ItemListener;

import java.util.List;

public class MealActivity extends AppCompatActivity implements MealContract.View, ItemListener {

    private ActivityMealBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_meal);
        injectDependencies();
        initUi();
        initPresenter();
    }


    private void injectDependencies() {
        DaggerMealComponent.builder()
                .applicationComponent(CarbculatorApplication.get(this).getComponent())
                .mealModule(new MealModule(this))
                .build()
                .inject(this);
    }

    private void initUi() {

    }

    private void initPresenter() {

    }

    @Override
    public void onAction(int position) {

    }

    @Override
    public void showMeal(Meal meal) {

    }

    @Override
    public void showComponents(List<Component> components) {

    }

    @Override
    public void showNewComponent(Component component) {

    }

    @Override
    public void removeComponent(Component component, int index) {

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showPreviousUi() {

    }
}

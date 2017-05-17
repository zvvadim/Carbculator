package com.shakenbeer.nutrition.food;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityFoodBinding;
import com.shakenbeer.nutrition.model.Food;

import javax.inject.Inject;


public class FoodActivity extends AppCompatActivity implements FoodContract.View {

    private static final String FOOD_EXTRA = "com.shakenbeer.nutrition.food.foodExtra";

    public static void start(Context context, Food food) {
        Intent starter = new Intent(context, FoodActivity.class);
        starter.putExtra(FOOD_EXTRA, food);
        context.startActivity(starter);
    }

    @Inject
    FoodContract.Presenter presenter;

    private ActivityFoodBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food);
        injectDependencies();
        initUi();
        initPresenter();
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void injectDependencies() {
        DaggerFoodComponent.builder()
                .applicationComponent(CarbculatorApplication.get(this).getComponent())
                .foodModule(new FoodModule())
                .build()
                .inject(this);
    }

    private void initUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initPresenter() {
        presenter.attachView(this);
        Food food = getIntent().getParcelableExtra(FOOD_EXTRA);
        if (food == null) {
            food = new Food();
        }
        presenter.setFood(food);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showFood(Food food) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPreviousUi() {
        onBackPressed();
    }
}

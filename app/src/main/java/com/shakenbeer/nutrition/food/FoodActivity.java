package com.shakenbeer.nutrition.food;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityFoodBinding;
import com.shakenbeer.nutrition.model.Food;

import javax.inject.Inject;


public class FoodActivity extends AppCompatActivity implements FoodContract.View, FoodListener {

    public static final String FOOD_EXTRA = "com.shakenbeer.nutrition.food.foodExtra";
    public static final String FOOD_ID_EXTRA = "com.shakenbeer.nutrition.food.foodIdExtra";

    public static void start(Context context, Food food) {
        Intent starter = new Intent(context, FoodActivity.class);
        starter.putExtra(FOOD_EXTRA, food);
        context.startActivity(starter);
    }

    public static void startForResult(Activity activity, Food food, int requestCode) {
        Intent starter = new Intent(activity, FoodActivity.class);
        starter.putExtra(FOOD_EXTRA, food);
        activity.startActivityForResult(starter, requestCode);
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

        binding.setListener(this);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save) {
            presenter.onSavePerformed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showFood(Food food) {
        binding.setFood(food);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPreviousUi(long foodId) {
        Intent data = new Intent();
        data.putExtra(FOOD_ID_EXTRA, foodId);
        setResult(RESULT_OK, data);
        onBackPressed();
    }

    @Override
    public void onNameChanged(CharSequence value) {
        presenter.onNameChanged(value.toString());
    }

    @Override
    public void onUnitAmountChanged(CharSequence value) {
        int amount = value.length() > 0 ? Integer.parseInt(value.toString()) : 0;
        presenter.onUnitAmountChanged(amount);
    }

    @Override
    public void onUnitNameChanged(CharSequence value) {
        presenter.onUnitNameChanged(value.toString());
    }

    @Override
    public void onProteinChanged(CharSequence value) {
        presenter.onProteinChanged(parseFloat(value));
    }

    @Override
    public void onFatChanged(CharSequence value) {
        presenter.onFatChanged(parseFloat(value));
    }

    @Override
    public void onCarbsChanged(CharSequence value) {
        presenter.onCarbsChanged(parseFloat(value));
    }

    @Override
    public void onKcalChanged(CharSequence value) {
        presenter.onKcalChanged(parseFloat(value));
    }

    private float parseFloat(CharSequence value) {
        return value.length() > 0 ? Float.parseFloat(value.toString()) : 0f;
    }

    public void onSaveClick(View view) {
        presenter.onSavePerformed();
    }
}

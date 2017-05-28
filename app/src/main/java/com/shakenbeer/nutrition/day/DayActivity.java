package com.shakenbeer.nutrition.day;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityDayBinding;
import com.shakenbeer.nutrition.meal.MealActivity;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;

import java.util.List;

import javax.inject.Inject;

public class DayActivity extends AppCompatActivity implements DayContract.View {

    public static final String DAY_EXTRA = "com.shakenbeer.nutrition.day.dayExtra";

    @SuppressWarnings("WeakerAccess")
    @Inject
    DayContract.Presenter presenter;
    @SuppressWarnings("WeakerAccess")
    @Inject
    MealAdapter adapter;
    private ActivityDayBinding binding;

    public static void startForResult(Activity activity, Day day, int requestCode) {
        Intent starter = new Intent(activity, DayActivity.class);
        starter.putExtra(DAY_EXTRA, day);
        activity.startActivityForResult(starter, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_day);
        injectDependencies();
        initUi();
        initPresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Day day = getIntent().getParcelableExtra(DAY_EXTRA);
        presenter.obtainMeals(day);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    private void injectDependencies() {
        DaggerDayComponent.builder()
                .applicationComponent(CarbculatorApplication.get(this).getComponent())
                .dayModule(new DayModule((position, meal) -> presenter.onMealRemove(meal, position)))
                .build()
                .inject(this);
    }

    private void initUi() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.mealsRecyclerView.setLayoutManager(layoutManager);
        binding.mealsRecyclerView.addItemDecoration(
                new DividerItemDecoration(this, layoutManager.getOrientation()));
        binding.mealsRecyclerView.setAdapter(adapter);
        adapter.setItemClickListener((item, position, shared) -> presenter.onMealClick(item));
    }

    private void initPresenter() {
        presenter.attachView(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            presenter.onAddMealClick();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void showDay(Day day) {
        binding.dayView.setDay(day);
        Intent data = new Intent();
        data.putExtra(DAY_EXTRA, day);
        setResult(RESULT_OK, data);
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.setItems(meals);
    }

    @Override
    public void showMealUi(Meal meal) {
        MealActivity.start(this, meal);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showMealRemoved(int position) {
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void showPreviousUi() {
        onBackPressed();
    }
}

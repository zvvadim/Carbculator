package com.shakenbeer.nutrition.day;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.databinding.ActivityDayBinding;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Meal;

import java.util.List;

import javax.inject.Inject;

public class DayActivity extends AppCompatActivity implements DayContract.View {

    private static final String DAY_EXTRA = "com.shakenbeer.nutrition.day.dayExtra";

    @Inject
    DayContract.Presenter presenter;
    @Inject
    MealAdapter adapter;
    private ActivityDayBinding binding;

    public static void start(Context context, Day day) {
        Intent starter = new Intent(context, DayActivity.class);
        starter.putExtra(DAY_EXTRA, day);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_day);
        injectDependencies();
        initUi();
        initPresenter();
    }

    private void injectDependencies() {
        DaggerDayComponent.builder()
                .applicationComponent(CarbculatorApplication.get(this).getComponent())
                .dayModule(new DayModule())
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
    }

    private void initPresenter() {
        presenter.attachView(this);
        Day day = getIntent().getParcelableExtra(DAY_EXTRA);
        presenter.obtainMeals(day);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add_eating_menu_item) {
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
    }

    @Override
    public void showMeals(List<Meal> meals) {
        adapter.addItems(meals);
    }

    @Override
    public void showMealUi(Meal meal) {

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

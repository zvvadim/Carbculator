package com.shakenbeer.nutrition.calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.day.DayActivity;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.meal.MealActivity;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.EndlessRecyclerViewScrollListener;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;

public class CalendarView extends RecyclerView implements CalendarContract.View,
        MainActivity.Callbacks {

    private static final int NEW_MEAL_REQUEST_CODE = 9526;
    private static final int EXISTING_DAY_REQUEST_CODE = 4945;

    @Inject
    CalendarContract.Presenter presenter;
    @Inject
    DayAdapter adapter;

    private Activity activity;

    public CalendarView(Activity context) {
        super(context);
        this.activity = context;
        injectDependencies(context);
        initUi(context);
        initListeners();
    }

    private void injectDependencies(Context context) {
        DaggerCalendarComponent.builder()
                .applicationComponent(CarbculatorApplication.get(context).getComponent())
                .calendarModule(new CalendarModule())
                .build()
                .inject(this);
    }

    private void initUi(Context context) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        setLayoutManager(layoutManager);
        addItemDecoration(new DividerItemDecoration(context, layoutManager.getOrientation()));
        setAdapter(adapter);
    }

    private void initListeners() {
        adapter.setItemClickListener((day, position, shared) -> presenter.onDayClick(day));

        addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.obtainDays();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.attachView(this);
        presenter.obtainDays();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();
    }

    @Override
    public void clear() {
        adapter.clear();
    }

    @Override
    public void showDays(List<Day> days) {
        adapter.addItems(days);
        showTodayIfNoData();
    }

    private void showTodayIfNoData() {
        if (adapter.getItemCount() == 0) {
            Day day = new Day();
            day.setDate(new Date());
            adapter.addItem(day);
        }
    }

    @Override
    public void showDayUi(Day day) {
        DayActivity.startForResult(getActivity(), day, EXISTING_DAY_REQUEST_CODE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showNewMealUi() {
        MealActivity.startForResult(getActivity(), new Meal(new Date()), NEW_MEAL_REQUEST_CODE);
    }

    @Override
    public void showDayUpdated(Day day, int position, boolean existing) {
        if (existing) {
            adapter.replaceItem(day, position);
        } else {
            adapter.addItem(day, position);
        }
    }

    @Override
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_MEAL_REQUEST_CODE && resultCode == RESULT_OK) {
            long mealId = data.getLongExtra(MealActivity.MEAL_ID_EXTRA, 0);
            if (mealId > 0) {
                presenter.onDayGrow(mealId, adapter.getItems());
            }
        }
        if (requestCode == EXISTING_DAY_REQUEST_CODE && resultCode == RESULT_OK) {
            Day day = data.getParcelableExtra(DayActivity.DAY_EXTRA);
            if (day != null) {
                presenter.onDayUpdated(day, adapter.getItems());
            }
        }
    }
}

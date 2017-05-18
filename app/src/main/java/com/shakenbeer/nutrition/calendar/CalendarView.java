package com.shakenbeer.nutrition.calendar;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.day.DayActivity;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.EndlessRecyclerViewScrollListener;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class CalendarView extends RecyclerView implements CalendarContract.View,
        MainActivity.Callbacks {

    @Inject
    CalendarContract.Presenter presenter;
    @Inject
    DayAdapter adapter;

    public CalendarView(Context context) {
        super(context);
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
        adapter.setItemClickListener(new BindingAdapter.ItemClickListener<Day>() {
            @Override
            public void onClick(Day day, int position, View... shared) {
                presenter.onDayClick(day);
            }
        });

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
        DayActivity.start(getContext(), day);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
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
    public void onNewMeal(long mealId) {
        presenter.onDayGrow(mealId, adapter.getItems());
    }

    @Override
    public void onNewFood(long foodId) {
        //not interested, so do nothing
    }
}

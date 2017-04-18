package com.shakenbeer.nutrition.calendar;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.day.DayActivity;
import com.shakenbeer.nutrition.model.Day;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.EndlessRecyclerViewScrollListener;

import java.util.List;

import javax.inject.Inject;

public class CalendarView extends RecyclerView implements CalendarContract.View {

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
        super.onDetachedFromWindow();
        presenter.detachView();
    }

    @Override
    public void showDays(List<Day> days) {
        adapter.addItems(days);
    }

    @Override
    public void showDayUi(Day day) {
        DayActivity.start(getContext(), day);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}

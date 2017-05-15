package com.shakenbeer.nutrition.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.calendar.CalendarView;
import com.shakenbeer.nutrition.foodlist.FoodListView;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private MainContainer container;

    private MainContract.Presenter presenter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_calendar:
                    presenter.onCalendarClick();
                    return true;
                case R.id.navigation_food_list:
                    presenter.onFoodListClick();
                    return true;
                case R.id.navigation_statistics:
                    presenter.onStatisticsClick();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenter = new MainPresenter();
        presenter.attachView(this);

        container = (MainContainer) findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onShow();
    }

    @Override
    public void showCalendarUi() {
        container.replace(new CalendarView(this));
    }

    @Override
    public void showFoodListUi() {
        container.replace(new FoodListView(this));
    }

    @Override
    public void showStatisticUi() {

    }

    @Override
    public void showSettingsUi() {

    }

    @Override
    public void showNewMealUi() {

    }

    private void clearContent() {
        if (container.getChildAt(0) != null) {
            container.removeViewAt(0);
        }
    }
}

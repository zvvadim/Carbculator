package com.shakenbeer.nutrition.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.calendar.CalendarContract;
import com.shakenbeer.nutrition.calendar.CalendarView;
import com.shakenbeer.nutrition.foodlist.FoodListContract;
import com.shakenbeer.nutrition.foodlist.FoodListView;

public class MainActivity extends AppCompatActivity {

    private static final int CALENDAR = 1;
    private static final int FOOD_LIST = 2;
    private static final int STATISTICS = 3;
    private static final int SETTINGS = 4;

    private int currentPage = CALENDAR;



    private MainContainer container;

    private Callbacks callbacks;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_calendar:
                    showCalendarUi();
                    currentPage = CALENDAR;
                    return true;
                case R.id.navigation_food_list:
                    showFoodListUi();
                    currentPage = FOOD_LIST;
                    return true;
                case R.id.navigation_statistics:
                    showStatisticUi();
                    currentPage = STATISTICS;
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (MainContainer) findViewById(R.id.content);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        onShow();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            if (currentPage == CALENDAR) {
                ((CalendarContract.View) callbacks).showNewMealUi();
            }
            if (currentPage == FOOD_LIST) {
                ((FoodListContract.View) callbacks).showNewFoodUi();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void onShow() {
        switch (currentPage) {
            case CALENDAR:
                showCalendarUi();
                break;
            case FOOD_LIST:
                showFoodListUi();
                break;
            case STATISTICS:
                showStatisticUi();
                break;
            case SETTINGS:
                showStatisticUi();
                break;
            default:
                showCalendarUi();
        }
    }

    public void showCalendarUi() {
        CalendarView calendarView = new CalendarView(this);
        callbacks = calendarView;
        container.replace(calendarView);
    }

    public void showFoodListUi() {
        FoodListView foodListView = new FoodListView(this);
        callbacks = foodListView;
        container.replace(foodListView);
    }

    public void showStatisticUi() {
        clearContent();
        callbacks = null;
    }

    private void clearContent() {
        if (container.getChildAt(0) != null) {
            container.removeViewAt(0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbacks != null) {
            callbacks.onActivityResult(requestCode, resultCode, data);
        }
    }

    public interface Callbacks {
        Activity getActivity();

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}

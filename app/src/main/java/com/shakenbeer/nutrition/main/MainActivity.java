package com.shakenbeer.nutrition.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.shakenbeer.nutrition.R;
import com.shakenbeer.nutrition.calendar.CalendarView;
import com.shakenbeer.nutrition.foodlist.FoodListView;
import com.shakenbeer.nutrition.meal.MealActivity;
import com.shakenbeer.nutrition.model.Meal;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int CALENDAR = 1;
    private static final int FOOD_LIST = 2;
    private static final int STATISTICS = 3;
    private static final int SETTINGS = 4;

    private int currentPage = CALENDAR;

    private static final int NEW_MEAL_REQUEST_CODE = 9526;
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        onShow();
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
        container.replace(new CalendarView(this));
    }

    public void showFoodListUi() {
        container.replace(new FoodListView(this));
    }

    public void showStatisticUi() {

    }

    public void showSettingsUi() {

    }

    public void showNewMealUi() {
        MealActivity.startForResult(this, new Meal(new Date()), NEW_MEAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_MEAL_REQUEST_CODE && resultCode == RESULT_OK) {
            Meal meal = data.getParcelableExtra(MealActivity.MEAL_EXTRA);
            if (callbacks != null) {
                callbacks.onNewMeal(meal);
            }
        }
    }

    private void clearContent() {
        if (container.getChildAt(0) != null) {
            container.removeViewAt(0);
        }
    }

    public interface Callbacks {
        void onNewMeal(Meal meal);
    }
}

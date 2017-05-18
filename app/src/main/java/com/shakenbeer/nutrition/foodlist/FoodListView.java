package com.shakenbeer.nutrition.foodlist;

import android.content.Context;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.food.FoodActivity;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.EndlessRecyclerViewScrollListener;

import java.util.List;

import javax.inject.Inject;


public class FoodListView extends RecyclerView implements FoodListContract.View,
        MainActivity.Callbacks {

    @Inject
    FoodListContract.Presenter presenter;
    @Inject
    FoodAdapter adapter;

    private FoodListener foodListener = new FoodListener() {
        @Override
        public void onDelete(int position, Food food) {
            presenter.onRemoveFood(position, food);
        }
    };

    public FoodListView(Context context) {
        super(context);
        injectDependencies(context);
        initUi(context);
        initListeners();
    }

    private void injectDependencies(Context context) {
        DaggerFoodListComponent.builder()
                .applicationComponent(CarbculatorApplication.get(context).getComponent())
                .foodListModule(new FoodListModule(foodListener))
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
        adapter.setItemClickListener(new BindingAdapter.ItemClickListener<Food>() {
            @Override
            public void onClick(Food food, int position, View... shared) {
                presenter.onFoodClick(food);
            }
        });

        addOnScrollListener(new EndlessRecyclerViewScrollListener((LinearLayoutManager) getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                presenter.obtainFoods();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        presenter.attachView(this);
        presenter.obtainFoods();
    }

    @Override
    protected void onDetachedFromWindow() {
        presenter.detachView();
        super.onDetachedFromWindow();

    }

    @Override
    public void showFoods(List<Food> foods) {
        adapter.addItems(foods);
    }

    @Override
    public void showFoodUi(Food food) {
        FoodActivity.start(getContext(), food);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void removeFood(int position, Food food) {
        adapter.removeItem(position);
    }

    @Override
    public void onNewMeal(long mealId) {
        //no interested, so do nothing
    }

    @Override
    public void onNewFood(long foodId) {
//        adapter.addItem(food);
//        smoothScrollToPosition(adapter.getItemCount() - 1);
        presenter.onNewFood(foodId);
    }
}

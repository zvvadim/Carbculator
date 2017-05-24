package com.shakenbeer.nutrition.foodlist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.shakenbeer.nutrition.CarbculatorApplication;
import com.shakenbeer.nutrition.food.FoodActivity;
import com.shakenbeer.nutrition.main.MainActivity;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.util.ui.BindingAdapter;
import com.shakenbeer.nutrition.util.ui.EndlessRecyclerViewScrollListener;

import java.util.List;

import javax.inject.Inject;

import static android.app.Activity.RESULT_OK;


public class FoodListView extends RecyclerView implements FoodListContract.View,
        MainActivity.Callbacks {

    private static final int NEW_FOOD_REQUEST_CODE = 3844;

    @Inject
    FoodListContract.Presenter presenter;
    @Inject
    FoodAdapter adapter;

    private Activity activity;

    public FoodListView(Activity context) {
        super(context);
        this.activity = context;
        injectDependencies(context);
        initUi(context);
        initListeners();
    }

    private void injectDependencies(Context context) {
        DaggerFoodListComponent.builder()
                .applicationComponent(CarbculatorApplication.get(context).getComponent())
                .foodListModule(new FoodListModule((position, food) ->
                        presenter.onRemoveFood(position, food)))
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
        adapter.setItemClickListener((food, position, shared) -> presenter.onFoodClick(food));

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
    public void showNewFoodUi() {
        FoodActivity.startForResult(getActivity(), new Food(), NEW_FOOD_REQUEST_CODE);
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
    public Activity getActivity() {
        return activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_FOOD_REQUEST_CODE && resultCode == RESULT_OK) {
            long foodId = data.getLongExtra(FoodActivity.FOOD_ID_EXTRA, 0);
            if (foodId > 0) {
                presenter.onNewFood(foodId);
            }
        }
    }
}

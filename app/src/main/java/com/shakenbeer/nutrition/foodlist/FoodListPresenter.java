package com.shakenbeer.nutrition.foodlist;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Food;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoodListPresenter extends FoodListContract.Presenter {

    private static final int OFFSET = 100;
    private final NutritionLab2 nutritionLab2;
    private int page = 0;
    private boolean everythingIsHere = false;

    @Inject
    public FoodListPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainFoods() {
        if (everythingIsHere) return;
        nutritionLab2.getFoodsRx(page, OFFSET)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Food>>() {
                    @Override
                    public void accept(@NonNull List<Food> foods) throws Exception {
                        processFoods(foods);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });
    }

    private void processFoods(List<Food> foods) {
        page++;
        getMvpView().showFoods(foods);
        if (foods.size() < OFFSET) {
            thatsAll();
        }
    }

    private void thatsAll() {
        everythingIsHere = true;
    }

    @Override
    void onFoodClick(Food food) {
        getMvpView().showFoodUi(food);
    }

    @Override
    void onNewFoodClick() {
        getMvpView().showFoodUi(new Food());
    }

    @Override
    void onRemoveFood(int position, Food food) {
        nutritionLab2.deleteFoodRx(food)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> getMvpView().removeFood(position, food), throwable ->
                        getMvpView().showError(throwable.getLocalizedMessage()));
    }

    @Override
    void onNewFood(long foodId) {

    }
}

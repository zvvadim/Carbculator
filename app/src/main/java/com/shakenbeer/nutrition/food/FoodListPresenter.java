package com.shakenbeer.nutrition.food;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Food;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class FoodListPresenter extends FoodListContract.Presenter {

    private static final int OFFSET = 100;
    private final NutritionLab2 nutritionLab2;
    private int page = 0;

    @Inject
    public FoodListPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainFoods() {
        nutritionLab2.getFoodsRx(page, OFFSET)
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Food>>() {
                    @Override
                    public void accept(@NonNull List<Food> foods) throws Exception {
                        getMvpView().showFoods(foods);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });
    }

    @Override
    void onFoodClick(Food food) {
        getMvpView().showFoodUi(food);
    }

    @Override
    void onNewFoodClick() {
        getMvpView().showFoodUi(new Food());
    }
}

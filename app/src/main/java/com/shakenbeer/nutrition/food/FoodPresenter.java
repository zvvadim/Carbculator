package com.shakenbeer.nutrition.food;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Food;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

class FoodPresenter extends FoodContract.Presenter {

    private final NutritionLab2 nutritionLab2;
    private Food food;

    FoodPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void setFood(Food food) {
        this.food = food;
        getMvpView().showFood(food);
    }

    @Override
    void onNameChanged(String name) {
        food.setName(name);
    }

    @Override
    void onUnitAmountChanged(int amount) {
        food.setUnit(amount);
    }

    @Override
    void onUnitNameChanged(String unitName) {
        food.setUnitName(unitName);
    }

    @Override
    void onProteinChanged(float value) {
        food.setProteinPerUnit(value);
    }

    @Override
    void onFatChanged(float value) {
        food.setFatPerUnit(value);
    }

    @Override
    void onCarbsChanged(float value) {
        food.setCarbsPerUnit(value);
    }

    @Override
    void onKcalChanged(float value) {
        food.setKcalPerUnit(value);
    }

    @Override
    void onSavePerformed() {
        nutritionLab2.saveFoodRx(food)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(foodId -> getMvpView().showPreviousUi(foodId), throwable ->
                        getMvpView().showError(throwable.getLocalizedMessage()));
    }
}

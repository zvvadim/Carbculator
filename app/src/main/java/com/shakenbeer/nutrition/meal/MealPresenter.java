package com.shakenbeer.nutrition.meal;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MealPresenter extends MealContract.Presenter {

    private final NutritionLab2 nutritionLab2;

    @Inject
    public MealPresenter(NutritionLab2 nutritionLab2) {
        this.nutritionLab2 = nutritionLab2;
    }

    @Override
    void obtainComponents(Meal meal) {
        nutritionLab2.getComponentsRx(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Component>>() {
                    @Override
                    public void accept(@NonNull List<Component> components) throws Exception {
                        getMvpView().showComponents(components);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });
    }

    @Override
    void onAddComponentClick() {

    }

    @Override
    void onRemoveComponentClick(Component component) {

    }
}

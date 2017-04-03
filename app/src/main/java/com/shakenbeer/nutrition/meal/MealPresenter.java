package com.shakenbeer.nutrition.meal;


import com.shakenbeer.nutrition.data.NutritionLab2;
import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Food;
import com.shakenbeer.nutrition.model.Meal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MealPresenter extends MealContract.Presenter {

    private final NutritionLab2 nutritionLab2;
    private Meal meal;
    private List<Component> components;
    private List<Component> toDelete = new ArrayList<>();

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
                        MealPresenter.this.components = components;
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
        Component component = new Component();
        components.add(component);
        getMvpView().showNewComponent(component);
    }

    @Override
    void onRemoveComponentClick(Component component, int index) {
        if (component.getId() > 0) {
            toDelete.add(component);
        }
        components.remove(index);
        getMvpView().removeComponent(component, index);
    }

    @Override
    void onSaveClick() {
        nutritionLab2
                .saveMealRx(meal)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnSuccess(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long mealId) throws Exception {
                        saveComponents(mealId);
                    }
                })
                .toCompletable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        getMvpView().showPreviusUi();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getMvpView().showError(throwable.getLocalizedMessage());
                    }
                });
    }

    private void saveComponents(long mealId) {
        for (int i = 0; i < components.size(); i++) {
            nutritionLab2.saveComponent(components.get(i), mealId);
        }
        for (int i = 0; i < toDelete.size(); i++) {
            nutritionLab2.deleteComponent(toDelete.get(i));
        }
    }

    @Override
    void onMealTypeSelected(int type) {
        meal.setNumber(type);
    }

    @Override
    void onMealDateSelected(int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth, hour, minute);
        meal.setDate(calendar.getTime());
    }

    @Override
    void onMealTimeSelected(int hour, int minutes) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(meal.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        calendar = new GregorianCalendar(year, month, day, hour, minutes);
        meal.setDate(calendar.getTime());
    }

    @Override
    void onComponentFoodSelected(Component component, Food food) {
        component.setFoodId(food.getId());
        component.setFoodName(food.getName());
        component.setFoodUnitName(food.getUnitName());
    }

    @Override
    void onComponentAmountChanged(Component component, int amount) {
        component.setGrams(amount);
    }
}

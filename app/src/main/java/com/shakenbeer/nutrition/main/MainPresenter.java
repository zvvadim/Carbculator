package com.shakenbeer.nutrition.main;


public class MainPresenter extends MainContract.Presenter {

    @Override
    void onCalendarClick() {
        if (isViewAttached()) {
            getMvpView().showCalendarUi();
        }
    }

    @Override
    void onFoodListClick() {
        if (isViewAttached()) {
            getMvpView().showFoodListUi();
        }
    }

    @Override
    void onStatisticClick() {
        if (isViewAttached()) {
            getMvpView().showStatisticUi();
        }
    }

    @Override
    void onSettingsClick() {
        if (isViewAttached()) {
            getMvpView().showSettingsUi();
        }
    }

    @Override
    void onAddMealClick() {

    }
}

package com.shakenbeer.nutrition.main;


class MainPresenter extends MainContract.Presenter {

    private static final int CALENDAR = 1;
    private static final int FOOD_LIST = 2;
    private static final int STATISTICS = 3;
    private static final int SETTINGS = 4;

    private int currentPage = CALENDAR;


    @Override
    void onShow() {
        switch (currentPage) {
            case CALENDAR:
                getMvpView().showCalendarUi();
                break;
            case FOOD_LIST:
                getMvpView().showFoodListUi();
                break;
            case STATISTICS:
                getMvpView().showStatisticUi();
                break;
            case SETTINGS:
                getMvpView().showStatisticUi();
                break;
            default:
                getMvpView().showCalendarUi();
        }
    }

    @Override
    void onCalendarClick() {
        getMvpView().showCalendarUi();
        currentPage = CALENDAR;
    }

    @Override
    void onFoodListClick() {
        getMvpView().showFoodListUi();
        currentPage = FOOD_LIST;
    }

    @Override
    void onStatisticsClick() {
        getMvpView().showStatisticUi();
        currentPage = STATISTICS;
    }

    @Override
    void onSettingsClick() {
        getMvpView().showSettingsUi();
        currentPage = SETTINGS;
    }

    @Override
    void onAddMealClick() {

    }
}

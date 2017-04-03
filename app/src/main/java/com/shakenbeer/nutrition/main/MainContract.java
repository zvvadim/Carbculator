package com.shakenbeer.nutrition.main;


import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

public interface MainContract {
    interface MainView extends MvpView {
        void showCalendarUi();
        void showFoodsUi();
        void showStaticticUi();
        void showSettingsUi();
    }

    abstract class MainPresenter extends BasePresenter<MainView> {
        abstract void onCalendarClick();
        abstract void onFoodsClick();
        abstract void onStatisticClick();
        abstract void onSettingsClick();
    }
}

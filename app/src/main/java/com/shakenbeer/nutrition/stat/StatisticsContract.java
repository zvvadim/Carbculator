package com.shakenbeer.nutrition.stat;


import com.shakenbeer.nutrition.model.Statistics;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

public interface StatisticsContract {

    interface View extends MvpView {
        void showStatistics(Statistics statistics);
        void showError(String message);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainStatistics();
    }
}

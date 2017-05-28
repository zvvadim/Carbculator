package com.shakenbeer.nutrition.presentation;



interface MvpPresenter<V extends MvpView> {
    void attachView(V mvpView);

    void detachView();

    void onDestroyed();
}

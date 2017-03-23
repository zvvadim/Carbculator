package com.shakenbeer.nutrition.presentation;


public abstract class BasePresenter<T extends MvpView> implements MvpPresenter<T> {

    private T mvpView;

    @Override
    public void attachView(T mvpView) {
        this.mvpView = mvpView;
    }

    @Override
    public void detachView() {
        mvpView = null;
    }

    public boolean isViewAttached() {
        return mvpView != null;
    }

    public T getMvpView() {
        return mvpView;
    }

    @Override
    public void onDestroyed() {
        //
    }

}

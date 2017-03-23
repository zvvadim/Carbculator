package com.shakenbeer.nutrition.meal;


import com.shakenbeer.nutrition.model.Component;
import com.shakenbeer.nutrition.model.Meal;
import com.shakenbeer.nutrition.presentation.BasePresenter;
import com.shakenbeer.nutrition.presentation.MvpView;

import java.util.List;

public interface MealContract {

    interface View extends MvpView {
        void showComponents(List<Component> components);
        void showNewComponent();
        void removeComponent(Component component);
        void showError(String message);
    }

    abstract class Presenter extends BasePresenter<View> {
        abstract void obtainComponents(Meal meal);
        abstract void onAddComponentClick();
        abstract void onRemoveComponentClick(Component component);
    }
}

package com.shakenbeer.nutrition.meal;


import com.shakenbeer.nutrition.model.Component;

import java.util.List;

public interface MealContract {

    interface View {
        void showComponents(List<Component> components);
        void showNewComponent();
        void removeComponent(Component component);
    }

    interface Presenter {
        void obtainComponents(long mealId);
        void onAddComponentClick();
        void onRemoveComponentClick(Component component);
    }
}

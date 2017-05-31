package com.shakenbeer.nutrition.meal;

public interface ComponentListener {
    void onDelete(int position);

    void onSelectFood(int position);

    void onAmountChanged(int position, String amount);
}

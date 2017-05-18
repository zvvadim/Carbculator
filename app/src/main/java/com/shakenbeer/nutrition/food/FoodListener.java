package com.shakenbeer.nutrition.food;


public interface FoodListener {
    
    void onNameChanged(CharSequence value);
    
    void onUnitAmountChanged(CharSequence value);
    
    void onUnitNameChanged(CharSequence value);
    
    void onProteinChanged(CharSequence value);
    
    void onFatChanged(CharSequence value);
    
    void onCarbsChanged(CharSequence value);
    
    void onKcalChanged(CharSequence value);
}

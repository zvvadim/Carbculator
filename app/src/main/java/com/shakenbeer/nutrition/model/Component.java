package com.shakenbeer.nutrition.model;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class Component {
    private long id;
    private long foodId;
    private String foodName;
    private String foodUnitName;
    private int grams;

    public Component() {
        id = -1;
    }

    @Override
    public String toString() {
        return foodName + ", " + grams + " grams";
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFoodId() {
        return foodId;
    }

    public void setFoodId(long foodId) {
        this.foodId = foodId;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public int getGrams() {
        return grams;
    }

    public void setGrams(int grams) {
        this.grams = grams;
    }

    public String getFoodUnitName() {
        return foodUnitName;
    }

    public void setFoodUnitName(String foodUnitName) {
        this.foodUnitName = foodUnitName;
    }
    
    

}

package com.shakenbeer.nutrition.model;


public class Statistics {
    private int totalDays = 0;
    private float totalProtein = 0;
    private float totalFat = 0;
    private float totalCarbs = 0;
    private float totalKcal = 0;

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public float getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(float totalProtein) {
        this.totalProtein = totalProtein;
    }

    public float getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(float totalFat) {
        this.totalFat = totalFat;
    }

    public float getTotalCarbs() {
        return totalCarbs;
    }

    public void setTotalCarbs(float totalCarbs) {
        this.totalCarbs = totalCarbs;
    }

    public float getTotalKcal() {
        return totalKcal;
    }

    public void setTotalKcal(float totalKcal) {
        this.totalKcal = totalKcal;
    }
}

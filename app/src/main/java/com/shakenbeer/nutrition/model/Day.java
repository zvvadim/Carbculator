package com.shakenbeer.nutrition.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class Day implements Parcelable {

    private Date date;

    private float protein;

    private float fat;

    private float carbs;

    private float kcal;

    public Day() {
        date = new Date();
    }

    private Day(Parcel in) {
        protein = in.readFloat();
        fat = in.readFloat();
        carbs = in.readFloat();
        kcal = in.readFloat();
        date = new Date(in.readLong());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(carbs);
        dest.writeFloat(kcal);
        dest.writeLong(date.getTime());
    }

    public static final Parcelable.Creator<Day> CREATOR = new Parcelable.Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel in) {
            return new Day(in);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

    public String getPfcRatio() {
        float sum = protein + fat + carbs;
        int p = Math.round((protein / sum) * 100);
        int f = Math.round((fat / sum) * 100);
        int c = Math.round((carbs / sum) * 100);

        return p + "/" + f + "/" + c;
    }

    @Override
    public String toString() {
        return date.toString();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCarbs() {
        return carbs;
    }

    public void setCarbs(float carbs) {
        this.carbs = carbs;
    }

    public float getKcal() {
        return kcal;
    }

    public void setKcal(float kcal) {
        this.kcal = kcal;
    }

}

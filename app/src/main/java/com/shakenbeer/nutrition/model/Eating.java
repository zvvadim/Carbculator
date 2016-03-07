package com.shakenbeer.nutrition.model;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class Eating implements Parcelable {
    
    private long id;
    
    private Date date;
    
    private int number;

    private float protein;

    private float fat;

    private float carbs;

    private float kcal;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeInt(number);
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(carbs);
        dest.writeFloat(kcal);
        dest.writeLong(date.getTime());
        
    }
    
    public static final Parcelable.Creator<Eating> CREATOR = new Parcelable.Creator<Eating>() {
        @Override
        public Eating createFromParcel(Parcel in) {
            return new Eating(in);
        }

        @Override
        public Eating[] newArray(int size) {
            return new Eating[size];
        }
    };

    private Eating(Parcel in) {
        id = in.readLong();
        number = in.readInt();
        protein = in.readFloat();
        fat = in.readFloat();
        carbs = in.readFloat();
        kcal = in.readFloat();
        date = new Date(in.readLong());
    }
    
    public String getPfcRatio() {
        float sum = protein + fat + carbs;
        int p = Math.round((protein / sum) * 100);
        int f = Math.round((fat / sum) * 100);
        int c = Math.round((carbs / sum) * 100);

        return p + "/" + f + "/" + c;
    }
    
    public Eating(Date date) {
        id = -1;
        this.date = date;
    }

    public Eating() {
        id = -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
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

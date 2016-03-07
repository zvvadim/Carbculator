package com.shakenbeer.nutrition.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Sviatoslav Melnychenko
 *
 */
public class Food implements Parcelable {
    
    private static final int DEFAULT_UNIT_VALUE = 100;
    
    private long id;
    
    private String name;
    
    private float proteinPerUnit;

    private float fatPerUnit;
    
    private float carbsPerUnit;

    private float kcalPerUnit;
    
    private int unit;
    
    private String unitName;
    
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeFloat(proteinPerUnit);
        dest.writeFloat(fatPerUnit);
        dest.writeFloat(carbsPerUnit);
        dest.writeFloat(kcalPerUnit);
        dest.writeInt(unit);
        dest.writeString(unitName);
        
    }
    
    public static final Parcelable.Creator<Food> CREATOR = new Parcelable.Creator<Food>() {
        @Override
        public Food createFromParcel(Parcel in) {
            return new Food(in);
        }

        @Override
        public Food[] newArray(int size) {
            return new Food[size];
        }
    };

    private Food(Parcel in) {
        id = in.readLong();
        name = in.readString();
        proteinPerUnit = in.readFloat();
        fatPerUnit = in.readFloat();
        carbsPerUnit = in.readFloat();
        kcalPerUnit = in.readFloat();
        unit = in.readInt();
        unitName = in.readString();
    }
    

    public Food() {
        id = -1;
        unit = DEFAULT_UNIT_VALUE;
    }
    
    public String getPfcRatio() {
        float sum = proteinPerUnit + fatPerUnit + carbsPerUnit;
        int p = Math.round((proteinPerUnit / sum) * 100);
        int f = Math.round((fatPerUnit / sum) * 100);
        int c = Math.round((carbsPerUnit/ sum) * 100);

        return p + "/" + f + "/" + c;
    }

    @Override
    public String toString() {
        return name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getProteinPerUnit() {
        return proteinPerUnit;
    }

    public void setProteinPerUnit(float proteinPerUnit) {
        this.proteinPerUnit = proteinPerUnit;
    }

    public float getFatPerUnit() {
        return fatPerUnit;
    }

    public void setFatPerUnit(float fatPerUnit) {
        this.fatPerUnit = fatPerUnit;
    }

    public float getCarbsPerUnit() {
        return carbsPerUnit;
    }

    public void setCarbsPerUnit(float carbsPerUnit) {
        this.carbsPerUnit = carbsPerUnit;
    }

    public float getKcalPerUnit() {
        return kcalPerUnit;
    }

    public void setKcalPerUnit(float kcalPerUnit) {
        this.kcalPerUnit = kcalPerUnit;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }   
    
    
    
}

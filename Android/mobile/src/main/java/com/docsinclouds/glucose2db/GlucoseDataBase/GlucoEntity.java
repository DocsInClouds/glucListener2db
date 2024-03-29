package com.docsinclouds.glucose2db.GlucoseDataBase;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity (tableName = "GlucoEntity")
public class GlucoEntity {
    @PrimaryKey(autoGenerate = true)
    private int mId;

    @ColumnInfo(name = "timestamps")
    private long mTimestamp;

    @ColumnInfo(name = "glucoValues")
    private int mGlucoValue;

    @ColumnInfo(name = "glucoTrends")
    private double mGlucoTrend;

    public long getTimestamp() {
        return mTimestamp;
    }

    public int getId() {
        return mId;
    }

    public int getGlucoValue() {
        return mGlucoValue;
    }

    public double getGlucoTrend() {
        return mGlucoTrend;
    }

    public GlucoEntity(long timestamp, int glucoValue, double glucoTrend) {
        this.mId = 0; // is autogenerated -> Dont care
        this.mTimestamp = timestamp;
        this.mGlucoValue = glucoValue;
        this.mGlucoTrend = glucoTrend;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public void setGlucoValue(int mGlucoValue) {
        this.mGlucoValue = mGlucoValue;
    }

    public void setTimestamp(long mTimestamp) {
        this.mTimestamp = mTimestamp;
    }

    public void setGlucoTrend(double mGlucoTrend) {
        this.mGlucoTrend = mGlucoTrend;
    }


}

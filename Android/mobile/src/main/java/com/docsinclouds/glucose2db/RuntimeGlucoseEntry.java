package com.docsinclouds.glucose2db;

public class RuntimeGlucoseEntry {

    private double glucoseValue;
    private long timeStamp;
    private double trend;

    public RuntimeGlucoseEntry(double glucoseValue, double trend, long timeStamp) {
        super();
        this.glucoseValue = glucoseValue;
        this.timeStamp = timeStamp;
        this.trend = trend;
    }

    /*public RuntimeGlucoseEntry(){
        super();
    }*/

    public double getGlucoseValue() {   return glucoseValue;    }

    public double getGlucoseTrend() {
        return trend;
    }
    public long getTimeStamp() {   return timeStamp;    }

}

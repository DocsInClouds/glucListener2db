package com.docsinclouds.glucose2db.G5Model;

/**
 * Created by joeginley on 3/28/16.
 */
public enum TransmitterStatus {
    UNKNOWN, BRICKED, LOW, OK;

    public static TransmitterStatus getBatteryLevel(int b) {
        if (b > 0x81) {
            return BRICKED;
        }
        else {
            if (b == 0x81) {
                return LOW;
            }
            else if (b == 0x00) {
                return OK;
            }
            else {
                return UNKNOWN;
            }
        }
    }
}

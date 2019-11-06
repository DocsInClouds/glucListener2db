package com.docsinclouds.glucose2db.G5Model;

import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import lombok.NoArgsConstructor;

/**
 * Created by jamorham on 02/07/2018.
 *
 */

@NoArgsConstructor
public abstract class BaseGlucoseRxMessage extends BaseMessage {

    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG

    public TransmitterStatus status;
    public int status_raw;
    public int timestamp;
    public int unfiltered;
    public int filtered;
    public int sequence; // : UInt32
    public boolean glucoseIsDisplayOnly; // : Bool
    public int glucose; // : UInt16
    public int state; //: UInt8
    public int trend; // : Int8 127 = invalid


    CalibrationState calibrationState() {
        return CalibrationState.parse(state);
    }

    boolean usable() {
        return calibrationState().usableGlucose();
    }

    boolean insufficient() {
        return calibrationState().insufficientCalibration();
    }

    boolean OkToCalibrate() {
        return calibrationState().readyForCalibration();
    }

    public Double getTrend() {
        return trend != 127 ? ((double) trend) / 10d : Double.NaN;
    }

    public Integer getPredictedGlucose() {
        return null; // stub
    }

}
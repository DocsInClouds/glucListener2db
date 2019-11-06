package com.docsinclouds.glucose2db.G5Model;

import static com.docsinclouds.glucose2db.G5Model.DexTimeKeeper.getDexTime;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;

// created by jamorham

public class BackFillTxMessage extends BaseMessage {

    final byte opcode = 0x50;
    final int length = 20;

    public BackFillTxMessage(int startDexTime, int endDexTime) {
        init(opcode, length);
        data.put((byte) 0x5);
        data.put((byte) 0x2);
        data.put((byte) 0x0);
        data.putInt(startDexTime);
        data.putInt(endDexTime);
        data.put(new byte[6]);
        appendCRC();
        Log.d(TAG, "BackfillTxMessage dbg: " + HelperClass.bytesToHex(byteSequence));
    }

    public static BackFillTxMessage get(String id, long startTime, long endTime) {

        final int dexStart = getDexTime(id, startTime);
        final int dexEnd = getDexTime(id, endTime);
        if (dexStart < 1 || dexEnd < 1) {
            Log.e(TAG, "Unable to calculate start or end time for BackFillTxMessage");
            return null;
        }
        return new BackFillTxMessage(dexStart, dexEnd);
    }

}

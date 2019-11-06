package com.docsinclouds.glucose2db.G5Model;


// created by jamorham

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;

public class CalibrateTxMessage extends BaseMessage {

    final byte opcode = 0x34;
    final int length = 9;

    final int glucose;

    public CalibrateTxMessage(int glucose, int dexTime) {
        init(opcode, length);
        this.glucose = glucose;
        data.putShort((short) glucose);
        data.putInt(dexTime);
        appendCRC();
        Log.d(TAG, "CalibrateGlucoseTxMessage dbg: " + HelperClass.bytesToHex(byteSequence));
    }

}

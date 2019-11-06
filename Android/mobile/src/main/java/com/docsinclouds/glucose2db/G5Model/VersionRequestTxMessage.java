package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;

/**
 * Created by jamorham on 25/11/2016.
 */

public class VersionRequestTxMessage extends BaseMessage {

    static final byte opcode = 0x4A;

    public VersionRequestTxMessage() {
        init(opcode, 3);
        Log.e(TAG, "VersionTx dbg: " + HelperClass.bytesToHex(byteSequence));
    }
}


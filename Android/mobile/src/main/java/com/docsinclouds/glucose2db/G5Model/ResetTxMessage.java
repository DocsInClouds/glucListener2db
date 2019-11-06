package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;

// jamorham

class ResetTxMessage extends BaseMessage {
    static final byte opcode = 0x42;

    ResetTxMessage() {
        init(opcode, 3);
        Log.d(TAG, "ResetTx dbg: " + HelperClass.bytesToHex(byteSequence));
    }
}

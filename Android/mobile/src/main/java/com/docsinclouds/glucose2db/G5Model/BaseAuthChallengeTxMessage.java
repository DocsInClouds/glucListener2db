package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;

// jamorham

class BaseAuthChallengeTxMessage extends BaseMessage {
    static final byte opcode = 0x04;

    BaseAuthChallengeTxMessage(final byte[] challenge) {

        init(opcode, 9);
        data.put(challenge);
        byteSequence = data.array();
        Log.d(TAG, "BaseAuthChallengeTX: " + HelperClass.bytesToHex(byteSequence));
    }
}

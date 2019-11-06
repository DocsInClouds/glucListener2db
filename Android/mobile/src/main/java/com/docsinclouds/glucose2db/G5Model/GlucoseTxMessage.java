package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;

/**
 * Created by jamorham on 25/11/2016.
 */

public class GlucoseTxMessage extends BaseMessage {

    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG
    static final byte opcode = 0x30;

    public GlucoseTxMessage() {
        init(opcode, 3);
        Log.d(TAG, "GlucoseTx dbg: " + HelperClass.bytesToHex(byteSequence));
    }
}


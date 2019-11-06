package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;

/**
 * Created by jamorham on 25/11/2016.
 */

public class BatteryInfoTxMessage extends BaseMessage {

    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG
    static final byte opcode = 0x22;

    public BatteryInfoTxMessage() {
        init(opcode, 3);
        Log.e(TAG, "BatteryInfoTx dbg: " + HelperClass.bytesToHex(byteSequence));
    }
}


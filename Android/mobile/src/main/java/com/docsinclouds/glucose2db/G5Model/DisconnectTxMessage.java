package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import java.nio.ByteBuffer;

/**
 * Created by joeginley on 3/16/16.
 */
public class DisconnectTxMessage extends BaseMessage {
    byte opcode = 0x09;
    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG
    public DisconnectTxMessage() {
        data = ByteBuffer.allocate(1);
        data.put(opcode);

        byteSequence = data.array();
        Log.d(TAG,"DisconnectTX: "+ HelperClass.bytesToHex(byteSequence));
    }
}


package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by joeginley on 3/16/16.
 */
public class KeepAliveTxMessage extends BaseMessage {
    public static final int opcode = 0x06;
    private int time;

    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG

    public KeepAliveTxMessage(int time) {
        this.time = time;

        data = ByteBuffer.allocate(2);
        data.put(new byte[]{(byte) opcode, (byte) this.time});
        byteSequence = data.order(ByteOrder.LITTLE_ENDIAN).array();

        Log.d(TAG, "New KeepAliveRequestTxMessage: " + HelperClass.bytesToHex(byteSequence));

    }
}
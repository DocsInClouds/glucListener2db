package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import java.nio.ByteBuffer;

/**
 * Created by jcostik1 on 3/26/16.
 */
public class SensorTxMessage extends BaseMessage {
    byte opcode = 0x2e;
    byte[] crc = CRC.calculate(opcode);


    public SensorTxMessage() {
        data = ByteBuffer.allocate(3);
        data.put(opcode);
        data.put(crc);
        byteSequence = data.array();
        Log.d(TAG, "SensorTx dbg: " + HelperClass.bytesToHex(byteSequence));
    }
}

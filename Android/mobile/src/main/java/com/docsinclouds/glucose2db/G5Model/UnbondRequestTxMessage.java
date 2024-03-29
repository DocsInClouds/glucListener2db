package com.docsinclouds.glucose2db.G5Model;

import java.nio.ByteBuffer;

/**
 * Created by joeginley on 3/16/16.
 */
public class UnbondRequestTxMessage extends TransmitterMessage {
    byte opcode = 0x6;

    public UnbondRequestTxMessage() {
        data = ByteBuffer.allocate(1);
        data.put(opcode);

        byteSequence = data.array();
    }
}

package com.docsinclouds.glucose2db.G5Model;

import java.nio.ByteBuffer;

/**
 * Created by joeginley on 3/28/16.
 */
public class TransmitterTimeTxMessage extends TransmitterMessage {
    public static final byte opcode = 0x24;
    private final static byte[] crc = CRC.calculate(opcode);

    public TransmitterTimeTxMessage() {
        data = ByteBuffer.allocate(3);
        data.put(opcode);
        data.put(crc);
        byteSequence = data.array();
    }
}

package com.docsinclouds.glucose2db.G5Model;

import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import com.google.gson.annotations.Expose;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

// jamorham

public class BaseMessage {

    protected static final String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG
    static final int INVALID_TIME = 0xFFFFFFFF;
    @Expose
    long postExecuteGuardTime = 50;
    @Expose
    public volatile byte[] byteSequence;
    public ByteBuffer data;


    void init(final byte opcode, final int length) {
        data = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        data.put(opcode);
        if (length == 1) {
            getByteSequence();
        } else if (length == 3) {
            appendCRC();
        }
    }

    byte[] appendCRC() {
        data.put(FastCRC16.calculate(getByteSequence(), byteSequence.length - 2));
        return getByteSequence();
    }

    boolean checkCRC(byte[] data) {
        if ((data == null) || (data.length < 3)) return false;
        final byte[] crc = FastCRC16.calculate(data, data.length - 2);
        return crc[0] == data[data.length - 2] && crc[1] == data[data.length - 1];
    }

    byte[] getByteSequence() {
        return byteSequence = data.array();
    }

    long guardTime() {
        return postExecuteGuardTime;
    }

    static int getUnsignedShort(ByteBuffer data) {
        return ((data.get() & 0xff) + ((data.get() & 0xff) << 8));
    }

    static int getUnsignedByte(ByteBuffer data) {
        return ((data.get() & 0xff));
    }

    static int getUnixTime() {
        return (int) (HelperClass.tsl() / 1000);
    }
}
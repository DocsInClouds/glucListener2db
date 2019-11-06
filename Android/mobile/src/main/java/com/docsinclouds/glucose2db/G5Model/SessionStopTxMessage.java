package com.docsinclouds.glucose2db.G5Model;

import com.docsinclouds.glucose2db.HelperClass;

// created by jamorham

public class SessionStopTxMessage extends BaseMessage {

    final byte opcode = 0x28;
    final int length = 7;
    {
        postExecuteGuardTime = 1000;
    }

    SessionStopTxMessage(int stopTime) {

        init(opcode, length);
        data.putInt(stopTime);
        appendCRC();
    }

    SessionStopTxMessage(String transmitterId) {
        final int stopTime = DexTimeKeeper.getDexTime(transmitterId, HelperClass.tsl());
        init(opcode, 7);
        data.putInt(stopTime);
        appendCRC();
    }


}

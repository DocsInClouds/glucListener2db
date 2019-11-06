package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import lombok.NoArgsConstructor;

/**
 * Created by jamorham on 25/11/2016.
 *
 * Alternate mechanism for reading data using the transmitter's internal algorithm.
 *
 * initial packet structure cribbed from Loopkit
 */

@NoArgsConstructor
public class GlucoseRxMessage extends BaseGlucoseRxMessage {

    private final static String TAG = Ob1G5CollectionService.TAG; // meh // DIC: changed from G5CollectionService.TAG

    public static final byte opcode = 0x31;


    public GlucoseRxMessage(byte[] packet) {
        Log.d(TAG, "GlucoseRX dbg: " + HelperClass.bytesToHex(packet));
        if (packet.length >= 14) {
            data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
            if ((data.get() == opcode) && checkCRC(packet)) {

                data = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);

                status_raw = data.get(1);
                status = TransmitterStatus.getBatteryLevel(data.get(1));
                sequence = data.getInt(2);
                timestamp = data.getInt(6);


                int glucoseBytes = data.getShort(10); // check signed vs unsigned!!
                glucoseIsDisplayOnly = (glucoseBytes & 0xf000) > 0;
                glucose = glucoseBytes & 0xfff;

                state = data.get(12);
                trend = data.get(13);
                if (glucose > 13) {
                    unfiltered = glucose * 1000;
                    filtered = glucose * 1000;
                } else {
                    filtered = glucose;
                    unfiltered = glucose;
                }

                Log.d(TAG, "GlucoseRX: seq:" + sequence + " ts:" + timestamp + " sg:" + glucose + " do:" + glucoseIsDisplayOnly + " ss:" + status + " sr:" + status_raw + " st:" + CalibrationState.parse(state) + " tr:" + getTrend());

            }
        } else {
            Log.d(TAG, "GlucoseRxMessage packet length received wrong: " + packet.length);
        }

    }


}

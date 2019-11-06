package com.docsinclouds.glucose2db.G5Model;

import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;
import java.util.Arrays;

/**
 * Created by joeginley on 3/16/16.
 */
public class AuthChallengeRxMessage extends BaseMessage {
    public static final int opcode = 0x03;
    public byte[] tokenHash;
    public byte[] challenge;
    private final static String TAG = Ob1G5CollectionService.TAG; // meh
    public AuthChallengeRxMessage(byte[] data) {
        Log.d(TAG,"AuthChallengeRX: "+ HelperClass.bytesToHex(data));
        if (data.length >= 17) {
            if (data[0] == opcode) {
                tokenHash = Arrays.copyOfRange(data, 1, 9);
                challenge = Arrays.copyOfRange(data, 9, 17);
            }
        }
    }
}

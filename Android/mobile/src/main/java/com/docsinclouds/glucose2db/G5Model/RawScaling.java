package com.docsinclouds.glucose2db.G5Model;

// jamorham

import static com.docsinclouds.glucose2db.G5Model.Ob1G5StateMachine.usingG6;

public class RawScaling {

    public enum DType {
        G5, G6v1, G6v2
    }

    public static double scale(final long raw, final DType version, final boolean filtered) {
        switch (version) {
            case G6v1:
                return raw * 34;
            case G6v2:
                return (raw - 1151500000) / 110;
            default:
                return raw;
        }
    }

    public static double scale(final long raw, final String transmitter_id, final boolean filtered) {
        final boolean g6 = usingG6();

        if (!g6) {
            return scale(raw, DType.G5, filtered);
        } else {
            final boolean g6r2 = FirmwareCapability.isTransmitterG6Rev2(transmitter_id);
            return scale(raw, g6r2 ? DType.G6v2 : DType.G6v1, filtered);
        }

    }

}

package com.docsinclouds.glucose2db.G5Utils;

// jamorham

// TODO check this reference handling

import com.docsinclouds.glucose2db.BackgroundClass;
import com.polidea.rxandroidble.RxBleClient;
import java.util.concurrent.ConcurrentHashMap;

public class RxBleProvider {
    private static final ConcurrentHashMap<String, RxBleClient> singletons = new ConcurrentHashMap<>();

    public static synchronized RxBleClient getSingleton(final String name) {
        final RxBleClient cached = singletons.get(name);
        if (cached != null) return cached;
        final RxBleClient created = RxBleClient.create(BackgroundClass.getAppContext());
        singletons.put(name, created);
        return created;
    }

    public static RxBleClient getSingleton() {
        return getSingleton("base");
    }

}

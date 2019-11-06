package com.docsinclouds.glucose2db.G5Utils;


import android.app.Service;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Inevitable;

/**
 * Created by Emma Black on 12/25/14.
 */
public class ForegroundServiceStarter {

    private static final String TAG = "FOREGROUND";
    public static final int NotificationID = 8123;

    final private Service mService;
    final private Context mContext;
    final private boolean run_service_in_foreground;
    //final private Handler mHandler;


    public static boolean shouldRunCollectorInForeground() {
        // Force foreground with Oreo and above
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O )
                || Pref.getBoolean("run_service_in_foreground", true);
    }

    public ForegroundServiceStarter(Context context, Service service) {
        mContext = context;
        mService = service;
        //mHandler = new Handler(Looper.getMainLooper());

        run_service_in_foreground = shouldRunCollectorInForeground();
    }


    public void start() {
        if (mService == null) {
            Log.e(TAG, "SERVICE IS NULL - CANNOT START!");
            return;
        }
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving to foreground");
            // mHandler.post(new Runnable() {
            //     @Override
            //     public void run() {
            // TODO use constants
            //DIC
//            final long end = System.currentTimeMillis() + (60000 * 5);
//            final long start = end - (60000 * 60 * 3) - (60000 * 10);
            foregroundStatus();
            Log.d(TAG, "CALLING START FOREGROUND: " + mService.getClass().getSimpleName());
            //DIC: replaced following with own notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mService.startForeground(NotificationID,
                    HelperClass.createOngoingNotificationWithReading(null));
            }
            //     }
            // });
        }
    }

    public void stop() {
        if (run_service_in_foreground) {
            Log.d(TAG, "should be moving out of foreground");
            mService.stopForeground(true);
        }
    }

    protected void foregroundStatus() {
        Inevitable.task("foreground-status", 2000, () -> Log.d("XFOREGROUND", mService.getClass().getSimpleName() + (
            HelperClass.isServiceRunningInForeground(mService.getClass()) ? " is running in foreground" : " is not running in foreground")));
    }

}

package com.docsinclouds.glucose2db.G5Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.docsinclouds.glucose2db.BackgroundClass;
import com.docsinclouds.glucose2db.BuildConfig;
import com.docsinclouds.glucose2db.HelperClass;
import com.docsinclouds.glucose2db.Inevitable;
import com.docsinclouds.glucose2db.MainActivity;
import com.docsinclouds.glucose2db.Ob1G5CollectionService;

/**
 * Created by Emma Black on 12/22/14.
 */

// TODO everything started here must be capable of being foreground if it is started with compat - really everything should be started with compat!

public class CollectionServiceStarter {

  public static final String pref_run_wear_collector = "run_wear_collector"; // only used on wear but here for code compatibility

  private Context mContext;
  private static final String TAG = CollectionServiceStarter.class.getSimpleName();

  private static final Object lock = new Object();

  private static volatile boolean stopPending;
  private static volatile boolean startPending;


  private static void queueRestart() {
    Log.d(TAG, "queueRestart called");
    if (!operationInProgress()) {
      Log.d(TAG, "Aquiring lock");
      synchronized (lock) {
        if (!operationInProgress()) {
          if (HelperClass.ratelimit("collection-queue-restart", 10)) {
            Log.d(TAG, "before: " + status());
            stopPending = true;
            startPending = true;
            Log.d(TAG, " after: " + status());
            automata();
          } else {
            Log.d(TAG, "Too fast - queueing cooldown task");
            Inevitable.task("collect-queue-cooldown", 3000, CollectionServiceStarter::queueRestart);
          }
        } else {
          Log.d(TAG, "Went busy during lock acquire: " + status());
        }
      }
    } else {
      Log.d(TAG, "Apparent operation already in progress so calling automata instead");
      automata();
    }
  }

  private static boolean operationInProgress() {
    return startPending || stopPending;
  }

  private static String status() {
    return String.format("Stop Pending: %b  Start Pending: %b", stopPending, startPending);
  }

  private static void automata() {
    Inevitable.task("collect-automata", 200, CollectionServiceStarter::processPending);
  }

  private static void processPending() {
    final PowerManager.WakeLock wl = HelperClass.getWakeLock("collection-processPending", 60000);
    try {
      synchronized (lock) {
        if (operationInProgress()) {
          // TODO staticify
          final CollectionServiceStarter starter = new CollectionServiceStarter(BackgroundClass.getAppContext());
          if (stopPending) {
            Log.d(TAG, "processPending: Issuing a stop all");
            starter.stopAll();
            stopPending = false;
          }
          if (startPending) {
            Log.d(TAG, "processPending: Issuing a start");
            starter.start();
            startPending = false;
          }
        } else {
          Log.d(TAG, "Called but nothing pending");
        }
      }
    } finally {
      HelperClass.releaseWakeLock(wl);
    }
  }

  // TODO refactor with dexcollection type
  public static boolean isFollower(Context context) {
    return Pref.getString("dex_collection_method", "").equals("Follower");
  }

  public static boolean isWifiandBTWixel(Context context) {
    return Pref.getString("dex_collection_method", "BluetoothWixel").equals("WifiBlueToothWixel");
  }

  public static boolean isWifiandBTLibre(Context context) {
    return Pref.getString("dex_collection_method", "BluetoothWixel").equals("LimiTTerWifi");
  }

  // are we in the specifc mode supporting wifi and dexbridge at the same time
  private static boolean isWifiandDexBridge() {
    return DexCollectionType.getDexCollectionType() == DexCollectionType.WifiDexBridgeWixel;
  }

  // are we in any mode which supports dexbridge
  public static boolean isDexBridgeOrWifiandDexBridge() {
    return isWifiandDexBridge() || isDexbridgeWixel(BackgroundClass.getAppContext());
  }

  public static boolean isBTWixelOrLimiTTer(Context context) {
    String collection_method = Pref.getString("dex_collection_method", "BluetoothWixel");
    return isBTWixelOrLimiTTer(collection_method);
  }

  private static boolean isBTWixelOrLimiTTer(String collection_method) {
    return collection_method.equals("BluetoothWixel")
        || collection_method.equals("LimiTTer");
  }

  private static boolean isDexbridgeWixel(Context context) {
    String collection_method = Pref.getString("dex_collection_method", "BluetoothWixel");
    return collection_method.equals("DexbridgeWixel");
  }

  private static boolean isDexbridgeWixel(String collection_method) {
    return collection_method.equals("DexbridgeWixel");
  }

  public static boolean isBTShare(Context context) {
    String collection_method = Pref.getString("dex_collection_method", "BluetoothWixel");
    return collection_method.equals("DexcomShare");
  }

  private static boolean isBTShare(String collection_method) {
    return collection_method.equals("DexcomShare");
  }

  public static boolean isBTG5(Context context) {

    String collection_method = Pref.getString("dex_collection_method", "BluetoothWixel");
    return collection_method.equals("DexcomG5");
  }

  private static boolean isBTG5(String collection_method) {
    return collection_method.equals("DexcomG5");
  }

  public static boolean isWifiWixel(Context context) {
    return Pref.getString("dex_collection_method", "BluetoothWixel").equals("WifiWixel");
  }

  public static boolean isWifiLibre(Context context) {
    return Pref.getString("dex_collection_method", "BluetoothWixel").equals("LibreWifi");
  }

  /*
   * LimiTTer emulates a BT-Wixel and works with the BT-Wixel service.
   * It would work without any changes but in some cases knowing that the data does not
   * come from a Dexcom sensor but from a Libre sensor might enhance the performance.
   * */

  public static boolean isLimitter() {
    return Pref.getStringDefaultBlank("dex_collection_method").equals("LimiTTer");
  }

  public static boolean isWifiandBTLibre() {
    return Pref.getStringDefaultBlank("dex_collection_method").equals("LimiTTerWifi");
  }


  private static boolean isWifiWixel(String collection_method) {
    return collection_method.equals("WifiWixel") || DexCollectionType.getDexCollectionType() == DexCollectionType.Mock;
  }

  private static boolean isWifiLibre(String collection_method) {
    return collection_method.equals("LibreWifi") || DexCollectionType.getDexCollectionType() == DexCollectionType.Mock;
  }


  private static boolean isFollower(String collection_method) {
    return collection_method.equals("Follower");
  }

  //  private static void newStart(final Context context) {
  //       new CollectionServiceStarter(context).start(context);
  //  }

  private void stopAll() {
    Log.d(TAG, "stop all");
//        stopBtShareService();
//        stopBtWixelService();
//        stopWifWixelThread();
//        stopFollowerThread();
    stopG5Service();
//        HelperClass.stopService(getCollectorServiceClass(Medtrum));
//        HelperClass.stopService(getCollectorServiceClass(NSFollow));
//        HelperClass.stopService(getCollectorServiceClass(SHFollow));
  }

  private void start(Context context, String collection_method) {
    Log.d(TAG, "start called: " + collection_method);
    this.mContext = context;
    MainActivity.checkAppContext(context);
    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.mContext);

//        if (isBTWixelOrLimiTTer(collection_method) || isDexbridgeWixel(collection_method)) {
//            Log.d("DexDrip", "Starting bt wixel collector");
//            stopWifWixelThread();
//            stopBtShareService();
//            stopFollowerThread();
//            stopG5Service();

//            if (prefs.getBoolean("wear_sync", false)) {//KS
//                boolean enable_wearG5 = prefs.getBoolean("enable_wearG5", false);
//                boolean force_wearG5 = prefs.getBoolean("force_wearG5", false);
//                startServiceCompat(WatchUpdaterService.class);
//                if (!enable_wearG5 || (enable_wearG5 && !force_wearG5)) { //don't start if Wear G5 Collector Service is active
//                    startBtWixelService();
//                }
//            } else {
//                startBtWixelService();
//            }
//        } else if (isWifiWixel(collection_method) || isWifiLibre(collection_method)) {
//            Log.d("DexDrip", "Starting wifi wixel collector");
//            stopBtWixelService();
//            stopFollowerThread();
//            stopBtShareService();
//            stopG5Service();
//
//            startWifWixelThread();
//        } else if (isBTShare(collection_method)) {
//            Log.d("DexDrip", "Starting bt share collector");
//            stopBtWixelService();
//            stopFollowerThread();
//            stopWifWixelThread();
//            stopG5Service();

//            if (prefs.getBoolean("wear_sync", false)) {//KS
//                boolean enable_wearG5 = prefs.getBoolean("enable_wearG5", false);
//                boolean force_wearG5 = prefs.getBoolean("force_wearG5", false);
//                startServiceCompat(new Intent(context, WatchUpdaterService.class));
//                if (!enable_wearG5 || (enable_wearG5 && !force_wearG5)) { //don't start if Wear G5 Collector Service is active
//                    startBtShareService();
//                }
//            }

//        } else
    if (isBTG5(collection_method)) {
      Log.d(TAG, "Starting G5 collector");
//            stopBtWixelService();
//            stopWifWixelThread();
//            stopBtShareService();

      startBtG5Service();

    } else {
      // TODO newer item startups should be consolidated in to a DexCollectionType has set to avoid duplicating logic
      if (DexCollectionType.hasBluetooth() ) {//|| DexCollectionType.getDexCollectionType() == NSFollow || DexCollectionType.getDexCollectionType() == SHFollow) {
        Log.d(TAG, "Starting service based on collector lookup");
        startServiceCompat(new Intent(context, DexCollectionType.getCollectorServiceClass()));
      }
    }

//        if (prefs.getBoolean("broadcast_to_pebble", false) && (PebbleUtil.getCurrentPebbleSyncType() != 1)) {
//            startPebbleSyncService();
//        }

    //startSyncService(); // TODO do we need to actually do this here?
    //startDailyIntentService();
    Log.d(TAG, collection_method);
  }

  private void start() {
    start(BackgroundClass.getAppContext(), Pref.getString("dex_collection_method", "BluetoothWixel"));
  }

  // private constructer, use static methods to start
  private CollectionServiceStarter(Context context) {
    if (context == null) context = BackgroundClass.getAppContext();
    this.mContext = context;
  }


  public static void restartCollectionServiceBackground() {
    Log.d(TAG, "restartCollectionServiceBackground Restart no args");
    Inevitable.task("restart-collection-service", 500, CollectionServiceStarter::queueRestart);
  }


  // TODO refactor all calls to this to use the background method above and make this private
  public static void restartCollectionService(Context context) {
    Log.d(TAG, "Restart with context");
    restartCollectionServiceBackground();
  }

  // called from wear
  public static void startBtService(final Context context) {
    Log.d(TAG, "startBtService: " + DexCollectionType.getDexCollectionType());
    stopBtService(context);
    final CollectionServiceStarter collectionServiceStarter = new CollectionServiceStarter(context);
    switch (DexCollectionType.getDexCollectionType()) {
      case DexcomShare:
        //collectionServiceStarter.startBtShareService();
        break;
      case DexcomG5:
        collectionServiceStarter.startBtG5Service();
        break;
      case Medtrum:
        //HelperClass.startService(getCollectorServiceClass(Medtrum));
      default:
        //collectionServiceStarter.startBtWixelService();
        break;
    }
  }

  public static void stopBtService(Context context) {
    Log.d(TAG, "stopBtService call stopService");
    final CollectionServiceStarter collectionServiceStarter = new CollectionServiceStarter(context);
//        collectionServiceStarter.stopBtShareService();
//        collectionServiceStarter.stopBtWixelService();
    collectionServiceStarter.stopG5Service();
    Log.d(TAG, "stopBtService should have called onDestroy");
  }


  private void startBtG5Service() {
    // Log.d(TAG,"stopping G5 service");
    // stopG5Service(); // TODO diabled due to multiple service restarts but others may suffer same problems - needs rework
    Log.d(TAG, "starting G5 service");
    //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
    Ob1G5CollectionService.keep_running = true;
    startServiceCompat(new Intent(this.mContext, Ob1G5CollectionService.class));
    //}
  }

    /*private void startSyncService() {
        Log.d(TAG, "starting Sync service");
        try {
            //HelperClass.startService(SyncService.class); // TODO update this for Oreo
            SyncService.startSyncServiceSoon();
        } catch (Exception e) {
            Log.wtf(TAG, "Failed to startSyncService: " + e);
        }
    }*/

   /* // TODO job scheduler???
    private void startDailyIntentService() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 4);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        final PendingIntent pi = PendingIntent.getService(this.mContext, 0, new Intent(this.mContext, DailyIntentService.class), PendingIntent.FLAG_UPDATE_CURRENT);
        final AlarmManager am = (AlarmManager) this.mContext.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
    }*/


  private void stopG5Service() {
    Log.d(TAG, "stopping G5  services");
//        G5CollectionService.keep_running = false; // ensure zombie stays down
//        this.mContext.stopService(new Intent(this.mContext, G5CollectionService.class));
    Ob1G5CollectionService.keep_running = false; // ensure zombie stays down
    this.mContext.stopService(new Intent(this.mContext, Ob1G5CollectionService.class));
    Ob1G5CollectionService.resetSomeInternalState();
  }

  private void startServiceCompat(final Class service) {
    startServiceCompat(new Intent(BackgroundClass.getAppContext(), service));
  }

  @SuppressWarnings("ConstantConditions")
  private void startServiceCompat(final Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        && BuildConfig.targetSDK >= Build.VERSION_CODES.N
        && ForegroundServiceStarter.shouldRunCollectorInForeground()) {
      try {
        Log.d(TAG, String.format("Starting oreo foreground service: %s", intent.getComponent().getClassName()));
      } catch (NullPointerException e) {
        Log.d(TAG, "Null pointer exception in startServiceCompat");
      }
      mContext.startForegroundService(intent);
    } else {
      mContext.startService(intent);
    }
  }

}

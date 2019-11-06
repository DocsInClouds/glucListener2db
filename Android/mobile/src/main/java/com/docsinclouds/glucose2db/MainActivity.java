package com.docsinclouds.glucose2db;

import static com.docsinclouds.glucose2db.Ob1G5CollectionService.OB1G5_PREFS;

import android.Manifest.permission;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.Toast;
import com.docsinclouds.glucose2db.G5Utils.CollectionServiceStarter;
import com.docsinclouds.glucose2db.G5Utils.DexCollectionType;
import com.docsinclouds.glucose2db.G5Utils.Pref;
import com.docsinclouds.glucose2db.G5Utils.WakeLockTrampoline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String GLUC_NOTIFICATION_CHANNEL_ID = "glucNotification1";
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;
    private static final String TAG = "MainActivity";
    public static Context contextOfApplication;
    protected static GlucoseViewModel mGlucoseViewModel;

    MenuItem settingsMenutItem;
    MenuItem clearDBMenuItem;

    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    private Handler mHandler = new Handler();
    private SharedPreferences sharedPreferences;
    private String transmitterId;
    private String dexcomType;

    public static Context getContextOfApplication() {
        return MainActivity.contextOfApplication;
    }

    public static boolean checkAppContext(Context context) {
        if (getContextOfApplication() == null) {
            MainActivity.contextOfApplication = context;
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }

        android.support.v7.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        stopAllServices();
    }

    private void stopAllServices() {
        PendingIntent pendingIntent = WakeLockTrampoline
            .getPendingIntent(Ob1G5CollectionService.class);
        HelperClass.cancelAlarm(this, pendingIntent);
        CollectionServiceStarter.stopBtService(this);
        stopNotificationChannel();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        contextOfApplication = getApplicationContext();
        mGlucoseViewModel = ViewModelProviders.of(this).get(GlucoseViewModel.class);

        setupSharedPreferences();

        // check for permissions
        List<String> permissionsNeeded = new ArrayList<>();
        final List<String> permissionsList = new ArrayList<>();
        if (!addPermission(permissionsList, permission.ACCESS_COARSE_LOCATION)) {
            permissionsNeeded.add("location");
        }

        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    permissionsList.toArray(new String[0]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        Log.d(TAG, "Permissions already present");

        initializeApp();
    }

    private void initializeApp() {
        final Switch toggleEnabled = findViewById(R.id.toggleRun);
        toggleEnabled.setOnClickListener(v -> {
            if (toggleEnabled.isChecked()) {
                Log.d("settings", "toggle enabled");
                settingsMenutItem.setEnabled(false);
                clearDBMenuItem.setEnabled(false);
                toggleServices(true);
            } else {
                Log.d("settings", "toggle disabled");
                settingsMenutItem.setEnabled(true);
                clearDBMenuItem.setEnabled(true);
                toggleServices(false);
            }
        });


    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return ActivityCompat
                .shouldShowRequestPermissionRationale(MainActivity.this, permission);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
        @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                    Log.d(TAG, "all permissions present");
                    initializeApp();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "Some Permission is Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /**
     * checks whether location services are enabled and directs the user to settings if not Location
     * services are needed for doing BLE scans on Android 6.0+
     */
    private void checkLocationService() {
        if (!BackgroundClass.isLocationEnabled(BackgroundClass.getAppContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.LocationServiceDialog_Title)
                .setMessage(R.string.LocationServiceDialog_Message)
                .setPositiveButton(R.string.LocationServiceDialog_EnterSettings,
                    (dialog, id) -> startActivity(
                        new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)))
                .setNegativeButton(R.string.settings_cancel,
                    (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    /**
     * used to show notifications on the smartphone
     */
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(GLUC_NOTIFICATION_CHANNEL_ID, name,
                    importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * stops the notification channel
     */
    private void stopNotificationChannel() {
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.deleteNotificationChannel(GLUC_NOTIFICATION_CHANNEL_ID);
        }
    }

    private void toggleServices(boolean start) {
        if (start) {
            transmitterId = sharedPreferences
                    .getString((getString(R.string.prefs_dexcomTransmitterId)), "4G0W4A").toUpperCase();
            dexcomType = sharedPreferences
                    .getString(getString(R.string.prefs_dexcomTransmitterType), "2");

            Pref.setBoolean("use_notification_channels", true);
            // set default settings for Dexcom devices
            Pref.setBoolean(OB1G5_PREFS, true);
            Pref.setBoolean("ob1_g5_use_transmitter_alg", true);
            HelperClass.setBuggySamsungEnabled(); //TODO dynamically set this

            Log.d("settings", "Use Dexcom Sensor");
            // location services are needed for BLE scans
            checkLocationService();

            Pref.setString(getString(R.string.prefs_dexcomTransmitterId), transmitterId);
            if (dexcomType.equals("2")) {
                Log.d(TAG, "Dexcom G6 selected");
                Ob1G5CollectionService.setG6Defaults();
            }
            DexCollectionType.setDexCollectionType(DexCollectionType.DexcomG5);

            // Create notification channel
            createNotificationChannel();

            // Start Dexcom Service
            CollectionServiceStarter.restartCollectionServiceBackground();

        }
        if (!start) {
            stopAllServices();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        settingsMenutItem = menu.findItem(R.id.more);
        clearDBMenuItem = menu.findItem(R.id.cleardb);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.more:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.cleardb:
                mGlucoseViewModel.deleteAll();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupSharedPreferences() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.prefs_dexcomTransmitterId))) {
            String dex_transm_id = sharedPreferences
                    .getString((getString(R.string.prefs_dexcomTransmitterId)), "4G0W4A").toUpperCase();
            if (!dex_transm_id.equals(transmitterId)) { // Transmitter ID was changed -> clear db
                Log.d("settings", "transmitter id was changed -> clearing db");
                mGlucoseViewModel.deleteAll();
            }
            Log.d("settings", "dexcom id = " + dex_transm_id);
        }

        if (key.equals(getString(R.string.prefs_dexcomTransmitterType))) {
            String dex_type = sharedPreferences
                    .getString((getString(R.string.prefs_dexcomTransmitterType)), "1");
            Log.d("settings", "dexcom id = " + dex_type);
        }
    }

    /**
     * close app if back is pressed twice consecutively
     */
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //      stopAllServices();
            super.onBackPressed();

            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        mHandler.postDelayed(mRunnable, 2000);
    }

}



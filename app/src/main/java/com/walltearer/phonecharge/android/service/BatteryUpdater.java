package com.walltearer.phonecharge.android.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BatteryUpdater extends Service {
    public static final String BATTERY_LEVEL_CHANGED = "BatteryUpdater.BATTERY_LEVEL_CHANGED";
    public static final String BATTERY_LEVEL = "BatteryUpdater.BATTERY_LEVEL";

    protected static final String LOG_TAG = "BatteryUpdater";

    protected BatteryManager batteryManager;
    protected BroadcastReceiver batteryUpdatesReceiver;
    protected DatabaseReference deviceDb;

    @Override
    public void onCreate() {
        super.onCreate();

        deviceDb = FirebaseDatabase.getInstance().getReference("device");
        batteryManager = (BatteryManager)getSystemService(BATTERY_SERVICE);

        updateBatteryLevel();

        listenToBatteryChanges();
    }

    protected void listenToBatteryChanges() {
        batteryUpdatesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateBatteryLevel();
            }
        };

        registerReceiver(batteryUpdatesReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    protected void updateBatteryLevel() {
        int batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);

        Log.d(LOG_TAG, "Battery level changed to: " + batteryLevel);

        notifyListenersOnBatteryLevel(batteryLevel);

        String deviceId = "1234-5678-ABCD"; // TODO: assign unique id to the phone, or retrieve it if it was already created

        deviceDb.child(deviceId).child("batteryLevel").setValue(batteryLevel);
    }

    protected void notifyListenersOnBatteryLevel(int batteryLevel) {
        Intent intent = new Intent();
        intent.setAction(BATTERY_LEVEL_CHANGED);
        intent.putExtra(BATTERY_LEVEL, batteryLevel);
        sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(batteryUpdatesReceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

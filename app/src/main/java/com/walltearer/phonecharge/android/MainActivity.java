package com.walltearer.phonecharge.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.walltearer.phonecharge.android.service.BatteryUpdater;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";

    TextView batteryLevelView;
    BroadcastReceiver batteryLevelReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateViewOnBatteryChanges();
        startBatteryUpdaterService();

/**
 * TODO:
 * - find a way to restrict logging depending on environment
 * - send battery level to the firebase
 * - ensure that app can run continuously in the background (and maybe can be autostarted?)
 * - when app is reopened, re-read the value immediately
 * - add tests
 */
    }

    protected void updateViewOnBatteryChanges() {
        batteryLevelView = (TextView) findViewById(R.id.chargeLevel);

        batteryLevelReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int batteryLevel = intent.getIntExtra(BatteryUpdater.BATTERY_LEVEL, 0);
                batteryLevelView.setText("" + batteryLevel);
            }
        };

        registerReceiver(batteryLevelReceiver, new IntentFilter(BatteryUpdater.BATTERY_LEVEL_CHANGED));
    }

    protected void startBatteryUpdaterService() {
        Intent batteryUpdater = new Intent(this, BatteryUpdater.class);
        startService(batteryUpdater);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        unregisterReceiver(batteryLevelReceiver);
    }
}

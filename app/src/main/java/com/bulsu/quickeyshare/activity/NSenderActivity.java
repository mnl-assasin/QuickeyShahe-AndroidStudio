package com.bulsu.quickeyshare.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bulsu.quickeyshare.R;

import java.util.List;

public class NSenderActivity extends AppCompatActivity {

    String TAG = NSenderActivity.class.getSimpleName();

    WifiManager mWifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsender);

        mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(mWifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        mWifiManager.startScan();
        Log.d(TAG, "onCreate");


    }

    private final BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                // add your logic here
                Log.d(TAG, "Size: " + mScanResults.size());

                for(int i = 0; i < mScanResults.size(); i++){
                    Log.d(TAG, "SSID: " + mScanResults.get(i).SSID + " Security: '" + mScanResults.get(i).capabilities);
                }


                connectToWifi();

            }
        }
    };

    private void connectToWifi() {



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mWifiScanReceiver);
    }
}

package com.bulsu.quickeyshare.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.bulsu.quickeyshare.ApManager;
import com.bulsu.quickeyshare.NetworkHelper;
import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.builder.DialogBuilder;

public class NReceiverActivity extends AppCompatActivity {

    String TAG = NReceiverActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nreceiver);

//        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(this)) {
                Log.d(TAG, "Can Write");
                checkWifiConnection();
            } else {
                Log.d(TAG, "Can't Write");
                showEnabledWriteSettings();
            }
        }
    }

    private void checkWifiConnection() {

        if (NetworkHelper.isWifiEnabled(this)) {
            DialogBuilder.createDialog(this, "Disable Wifi", "In order to receive files, you need to disable your Wifi connection", "Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    NetworkHelper.setWifiEnabled(NReceiverActivity.this, false);
                }
            }, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        }

        ApManager.configApState(this);
    }

    private void showEnabledWriteSettings() {
        DialogBuilder.createDialog(this, "Enable Write Settings", "Enabled the Allow write system Settings for QuickeyShare", "Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermission();

    }
}

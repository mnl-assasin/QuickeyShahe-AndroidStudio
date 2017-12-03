package com.bulsu.quickeyshare.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.Const;
import com.bulsu.quickeyshare.data.EZSharedPrefences;
import com.bulsu.quickeyshare.data.FileHelper;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = MainActivity.class.getSimpleName();


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    @Bind(R.id.ivSend)
    ImageView ivSend;
    @Bind(R.id.ivReceive)
    ImageView ivReceive;

    @Bind(R.id.activity_main)
    RelativeLayout activityMain;
    @Bind(R.id.navigationView)
    NavigationView navigationView;
    @Bind(R.id.drawerLayout)
    DrawerLayout drawerLayout;

    ActionBarDrawerToggle actionBarDrawerToggle;
    @Bind(R.id.ivDrawer)
    ImageView ivDrawer;
    @Bind(R.id.ivLock)
    ImageView ivLock;
    @Bind(R.id.tvReceived)
    TextView tvReceived;
    @Bind(R.id.containerReceived)
    LinearLayout containerReceived;
    @Bind(R.id.tvData)
    TextView tvData;
    @Bind(R.id.tvLockedFiles)
    TextView tvLockedFiles;
    @Bind(R.id.containerData)
    LinearLayout containerData;
    @Bind(R.id.containerLockFiles)
    LinearLayout containerLockFiles;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        checkPermissions();
        initialized();


    }

    private void initialized() {
        setupNavigation();
    }

    private void initData() {
        File fileDirectory = new File(Const.DEFAULT_FOLDER_PATH);
        File[] dirFiles = null;

        if (fileDirectory != null)
            if (fileDirectory.exists())
                dirFiles = fileDirectory.listFiles();

        if (dirFiles != null)
            if (dirFiles.length > 0) {
                tvReceived.setText(String.valueOf(dirFiles.length));
                tvData.setText(FileHelper.getTotalFileSize(dirFiles));
                Log.d(TAG, "Count: " + dirFiles.length);
            }
//        Log.d(TAG, "Count: " + dirFiles.length);


        File vault = new File(Const.DEFAULT_VAULT_PATH);
        File[] vaultFiles = null;
        if (vault != null)
            if (vault.exists())
                vaultFiles = vault.listFiles();

        if (vaultFiles != null)
            if (vaultFiles.length > 0) {
                tvLockedFiles.setText(String.valueOf(vaultFiles.length));
                Log.d(TAG, "Count: " + vaultFiles.length);
            }
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
//                fragmentFrame.setTranslationX(slideOffset * drawerView.getWidth());
//                drawerLayout.bringChildToFront(fragmentFrame);
//                drawerLayout.requestLayout();
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(false);
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(getApplicationContext(), AboutActivity.class));
                break;
            case R.id.menu_help:
                startActivity(new Intent(getApplicationContext(), HelpActivity.class));
                break;
            case R.id.menu_exit:
                displayExitDialog();
        }

        drawerLayout.closeDrawers();

        return true;
    }

    private void displayExitDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("QuickeyShare").setMessage("Are you sure you want to Exit?").setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).show();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        displayExitDialog();
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, REQUIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, REQUIRED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[2]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs read and write storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (EZSharedPrefences.getManifestWriteStorage(MainActivity.this)) { // WRITE STORAGE PERMISSION
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs read and write storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant read and write storage", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
            }

            Log.d(TAG, "Permissions required?");
//            txtPermissions.setText("Permissions Required");
//            SharedPreferences.Editor editor = permissionStatus.edit();
//            editor.putBoolean(REQUIRED_PERMISSIONS[0],true);
//            editor.commit();
        } else {

            Log.d(TAG, "Proceed to after permission process");
            initData();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        final String mTAG = "onRequestPermission: ";
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }


            if (allgranted) {
                Log.d(TAG, mTAG + "Proceed to after permission process");
//                proceedAfterPermission();
                initData();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[2])) {
                //txtPermissions.setText("Permissions Required");
                Log.d(TAG, mTAG + "All Permissions required");
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs read and write storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                //                proceedAfterPermission();
                Log.d(TAG, "onActivityResult: proceed to after permission process");
                initData();

            }
        }
    }

    @OnClick({R.id.ivDrawer, R.id.ivLock, R.id.ivSend, R.id.ivReceive, R.id.tvReceived, R.id.containerReceived, R.id.containerData, R.id.containerLockFiles})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDrawer:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

            case R.id.ivSend:
                startActivity(new Intent(getApplicationContext(), FileChooserActivity.class));
                break;
            case R.id.ivReceive:
                startActivity(new Intent(getApplicationContext(), ReceiverActivity.class));
                break;
            case R.id.ivLock:
                onLockFilesClick();
                break;
            case R.id.containerReceived:
            case R.id.containerData:
                startActivity(new Intent(getApplicationContext(), ReceiveFilesActivity.class));
                break;
            case R.id.containerLockFiles:
                onLockFilesClick();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void onLockFilesClick() {
//        EZSharedPrefences.setPinCode(this, "");
        String pin = EZSharedPrefences.getPinCode(this);
        String answer = EZSharedPrefences.getSecAnswer(this);
        if (pin.equals("") && answer.equals(""))
            startActivity(new Intent(this, NominatePinActivity.class));
        else
            startActivity(new Intent(this, PinActivity.class));
    }
}

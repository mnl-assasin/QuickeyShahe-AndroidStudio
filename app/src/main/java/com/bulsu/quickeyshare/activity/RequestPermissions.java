package com.bulsu.quickeyshare.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.EZSharedPrefences;
import com.bulsu.quickeyshare.data.ZipHelper;

import java.io.File;

public class RequestPermissions extends AppCompatActivity {

    private static final String TAG = "RequestPermissions";

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions);

        checkPermissions();
//        Log.d(TAG, "absolute filePath: " + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
//        createDirectory();

//        startActivity(new Intent(getApplicationContext(), ReadFileActivity.class));

//        decompress();

    }

    private void decompress() {
        File fDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare");
        String path = fDirectory.getAbsolutePath() + File.separator;
        if (ZipHelper.decompress(path, "compress.zip"))
            Log.d(TAG, "Decompress success");
        else
            Log.d(TAG, "Decompress fail");
    }

    private void createDirectory() {

        File fDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "QuickeyShare");

        if (!fDirectory.exists()) {
            if (fDirectory.mkdir())
                Log.d(TAG, fDirectory.getAbsolutePath() + " folder created");
        }

        String files[] = {"/storage/emulated/0/DCIM/Camera/20170212_190948.jpg", "/storage/emulated/0/DCIM/Camera/20170212_191348.jpg"};
        String zip = fDirectory.getAbsolutePath() + File.separator + "compress.zip";

        if (ZipHelper.compress(files, zip))
            Log.d(TAG, "Compression successful");
        else
            Log.d(TAG, "Compression failed");

    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(RequestPermissions.this, REQUIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(RequestPermissions.this, REQUIRED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissions.this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissions.this, REQUIRED_PERMISSIONS[1])) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestPermissions.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs read and write storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(RequestPermissions.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (EZSharedPrefences.getManifestWriteStorage(RequestPermissions.this)) { // WRITE STORAGE PERMISSION
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestPermissions.this);
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
                ActivityCompat.requestPermissions(RequestPermissions.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
            }

            Log.d(TAG, "Permissions required?");
//            txtPermissions.setText("Permissions Required");

//            SharedPreferences.Editor editor = permissionStatus.edit();
//            editor.putBoolean(REQUIRED_PERMISSIONS[0],true);
//            editor.commit();
        } else {
            //You already have the permission, just go ahead.
//            proceedAfterPermission();
            Log.d(TAG, "Proceed to after permission process");
            proceedAfterPermission();
        }
    }

    private void proceedAfterPermission() {
//        startActivity(new Intent(getApplicationContext(), MainActivity.class));
//        setResult(RESULT_OK);
        finish();
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
                proceedAfterPermission();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissions.this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(RequestPermissions.this, REQUIRED_PERMISSIONS[1])) {
                //txtPermissions.setText("Permissions Required");
                Log.d(TAG, mTAG + "All Permissions required");
                AlertDialog.Builder builder = new AlertDialog.Builder(RequestPermissions.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs read and write storage permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(RequestPermissions.this, REQUIRED_PERMISSIONS, PERMISSION_CALLBACK_CONSTANT);
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
            if (ActivityCompat.checkSelfPermission(RequestPermissions.this, REQUIRED_PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
//                proceedAfterPermission();
                Log.d(TAG, "onActivityResult: proceed to after permission process");
                proceedAfterPermission();

            }
        }
    }
}

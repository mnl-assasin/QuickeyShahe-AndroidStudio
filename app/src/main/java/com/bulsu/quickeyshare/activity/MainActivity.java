package com.bulsu.quickeyshare.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String TAG = MainActivity.class.getSimpleName();


    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
    @Bind(R.id.ivHistory)
    ImageView ivHistory;
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
            }
        Log.d(TAG, "Count: " + dirFiles.length);

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
        }

        drawerLayout.closeDrawers();

        return true;
    }

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, REQUIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, REQUIRED_PERMISSIONS[1]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[1])) {

                //Show Information about why you need the permission
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
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[1])) {
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
                //Got Permission
//                proceedAfterPermission();
                Log.d(TAG, "onActivityResult: proceed to after permission process");
//                proceedAfterPermission();
                initData();

            }
        }
    }


    byte[] key, iv;

    private void test() {
        // Get key
        key = getKey();
        // Get IV
        iv = getIV();

    }


    public void encryptFile(View view) {
        Bitmap bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() + "/img.png");
        // Write image data to ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        // Encrypt and save the image
        saveFile(encrypt(key, baos.toByteArray()), "enimg.png");
    }


    public void decryptFile(View view) {
        try {
            // Create FileInputStream to read from the encrypted image file
            FileInputStream fis = new FileInputStream(Environment.getExternalStorageDirectory() + "/enimg.png");
            // Save the decrypted image
            saveFile(decrypt(key, fis), "deimg.png");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    public void saveFile(byte[] data, String outFileName) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(Environment.getExternalStorageDirectory() + File.separator + outFileName);
            fos.write(data);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private byte[] encrypt(byte[] skey, byte[] data) {
        SecretKeySpec skeySpec = new SecretKeySpec(skey, "AES");
        Cipher cipher;
        byte[] encrypted = null;
        try {
            // Get Cipher instance for AES algorithm
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // Initialize cipher
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            // Encrypt the image byte data
            encrypted = cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    private byte[] decrypt(byte[] skey, FileInputStream fis) {

        SecretKeySpec skeySpec = new SecretKeySpec(skey, "AES");

        Cipher cipher;
        byte[] decryptedData = null;
        CipherInputStream cis = null;
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(iv));
            // Create CipherInputStream to read and decrypt the image data
            cis = new CipherInputStream(fis, cipher);
            // Write encrypted image data to ByteArrayOutputStream
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            while ((cis.read(data)) != -1) {
                buffer.write(data);
            }
            buffer.flush();
            decryptedData = buffer.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                cis.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return decryptedData;
    }

    private byte[] getKey() {
        KeyGenerator keyGen;
        byte[] dataKey = null;

        try {
            // Generate 256-bit key
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(256);
            SecretKey secretKey = keyGen.generateKey();
            dataKey = secretKey.getEncoded();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return dataKey;
    }

    private byte[] getIV() {
        SecureRandom random = new SecureRandom();
        byte[] iv = random.generateSeed(16);
        return iv;
    }


    @OnClick({R.id.ivDrawer, R.id.ivHistory, R.id.ivSend, R.id.ivReceive, R.id.tvReceived, R.id.containerReceived, R.id.containerData, R.id.containerLockFiles})
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

            case R.id.ivHistory:
            case R.id.containerReceived:
            case R.id.containerData:
                break;
            case R.id.containerLockFiles:
                break;
        }
    }
}

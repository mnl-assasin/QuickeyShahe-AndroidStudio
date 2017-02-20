package com.bulsu.quickeyshare.readfile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.bulsu.quickeyshare.R;
import com.bulsu.quickeyshare.data.EZSharedPrefences;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadFileActivity extends AppCompatActivity {
    private static final String TAG = "ReadFile";

    private static final String ROOT = "/storage/sdcard/dcim";
    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;

    @Bind(R.id.list)
    ListView list;

    ArrayList<String> targets;
    ArrayList<String> paths;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file);
        ButterKnife.bind(this);

//        checkPermission();
        readAllImage();
//        readStorage();
    }

    private void checkPermission() {

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ReadFileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadFileActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs phone permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (EZSharedPrefences.getManifestReadStorage(ReadFileActivity.this)) {
                //Previously Permission Request was cancelled wit 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ReadFileActivity.this);
                builder.setTitle("Need Storage Permission");
                builder.setMessage("This app needs storage permission.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getApplicationContext(), "Go to Permissions to Grant Phone", Toast.LENGTH_LONG).show();
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_PERMISSION_CONSTANT);
                }
            }
//            txtPermissions.setText("Permissions Required");

            EZSharedPrefences.setManifestReadStorage(ReadFileActivity.this, true);
        } else {
            //You already have the permission, just go ahead.
//            proceedAfterPermission();
            Log.d(TAG, "Access Already Granted");
//            readStorage();
            readAllImage();
            readAllVideo();
        }
    }

    private void readAllImage() {

        List<String> imagePath;
        imagePath = getImagesPath(ReadFileActivity.this);

        for (int ctr = 0; ctr < imagePath.size(); ctr++) {
            Log.d(TAG, "PATH: " + imagePath.get(ctr));
        }


    }

    public static ArrayList<String> getImagesPath(Activity activity) {
        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(0,PathOfImage);
        }
        return listOfAllImages;
    }

    private void readAllVideo() {
        List<String> videoPath;
        videoPath = getImagesPath(ReadFileActivity.this);

        for (int ctr = 0; ctr < videoPath.size(); ctr++) {
            Log.d(TAG, "Video Path: " + videoPath.get(ctr));
        }
    }

    public static ArrayList<String> getVideoPath(Activity activity) {

        Uri uri;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            listOfAllImages.add(PathOfImage);
        }
        return listOfAllImages;

    }

    private void readStorage() {
        List<String> imagePath = new ArrayList<>();
        File[] files = new File(Environment.getExternalStorageDirectory().getAbsolutePath()).listFiles(new ImageFileFilter());

        for (File file : files) {

            // Add the directories containing images or sub-directories
            if (file.isDirectory()
                    && file.listFiles(new ImageFileFilter()).length > 0) {

//                items.add(new GridViewItem(file.getAbsolutePath(), true, null));

                Log.d(TAG, "File Folder: " + file.getAbsolutePath());
            }
            // Add the images
            else {
//                Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
//                        50,
//                        50);
//                items.add(new GridViewItem(file.getAbsolutePath(), false, image));
                Log.d(TAG, "Image file: " + file.getAbsolutePath());
            }
        }
    }

    /**
     * Checks the file to see if it has a compatible extension.
     */
    private boolean isImageFile(String filePath) {
        return (filePath.endsWith(".jpg") || filePath.endsWith(".png"));
        // Add other formats as desired
//            return true;
//        else
//            return false;
    }

    private class ImageFileFilter implements FileFilter {

        @Override
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            } else if (isImageFile(file.getAbsolutePath())) {
                return true;
            }
            return false;
        }
    }


//    private List<GridViewItem> createGridItems(String directoryPath) {
//        List<GridViewItem> items = new ArrayList<GridViewItem>();
//
//        // List all the items within the folder.
//        File[] files = new File(directoryPath).listFiles(new ImageFileFilter());
//        for (File file : files) {
//
//            // Add the directories containing images or sub-directories
//            if (file.isDirectory()
//                    && file.listFiles(new ImageFileFilter()).length > 0) {
//
//                items.add(new GridViewItem(file.getAbsolutePath(), true, null));
//            }
//            // Add the images
//            else {
//                Bitmap image = BitmapHelper.decodeBitmapFromFile(file.getAbsolutePath(),
//                        50,
//                        50);
//                items.add(new GridViewItem(file.getAbsolutePath(), false, image));
//            }
//        }
//
//        return items;
//    }
//
//


}

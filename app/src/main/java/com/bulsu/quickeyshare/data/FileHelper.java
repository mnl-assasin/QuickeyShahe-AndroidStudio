package com.bulsu.quickeyshare.data;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class FileHelper {
    public static final String TAG = "FileHelper";

    public static String getFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getFileSize(String path) {

        int MB = 1024;
        int GB = MB * MB;

        File file = new File(path);
        int size = Integer.parseInt(String.valueOf(file.length() / 1024));
        float fSize = (float) size;
//        return String.valueOf(size);
        Log.d(TAG, "Size: " + size);
        if (size < 1) {
            return size * 1000 + " Bytes";
        } else if (size > 1 && size < MB)
            return size + " KB";
        else if (size > MB && size < GB)
            return String.format(Locale.getDefault(), "%.2f", fSize / MB) + " MB";
//        else
//            return (size / 1000000 + String.valueOf(size % 1000000).substring(0, 2)) + "GB";
        return "";
    }

    public static String getTotalFileSize(File files[]) {
        int MB = 1024;
        int GB = MB * MB;

        float total = 0;


        for (int ctr = 0; ctr < files.length; ctr++) {
            File file = files[ctr];
            float size = Float.parseFloat(String.valueOf(file.length() / 1024));
            total += size;
        }

        if (total < 1) {
            return total * 1000 + " Bytes";
        } else if (total > 1 && total < MB)
            return total + " KB";
        else if (total > MB && total < GB)
            return String.format(Locale.getDefault(), "%.2f", total / MB) + " MB";
//        else
//            return (size / 1000000 + String.valueOf(size % 1000000).substring(0, 2)) + "GB";
        return "";


    }

    public static ArrayList<String> getFilesPath(Activity activity) {
        Uri uri;
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
//        uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        uri = android.provider.MediaStore.Files.getContentUri("external");
        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Files.FileColumns.DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            list.add(0, PathOfImage);
        }
        logList("VideoFragment", list);
        return list;
    }

    public static ArrayList<String> getVideoPath(Activity activity) {
        Uri uri;
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            list.add(0, PathOfImage);
        }
        logList("VideoFragment", list);
        return list;
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

            listOfAllImages.add(0, PathOfImage);
        }
        return listOfAllImages;
    }

    public static ArrayList<String> getAudioPath(Activity activity) {
        Uri uri;
        ArrayList<String> list = new ArrayList<String>();
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        String PathOfImage = null;
        uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Audio.Media.TITLE};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        while (cursor.moveToNext()) {
            PathOfImage = cursor.getString(column_index_data);

            list.add(0, PathOfImage);
        }
        logList("VideoFragment", list);
        return list;
    }

    public static void logList(String TAG, List<String> list) {
        for (int i = 0; i < list.size(); i++)
            Log.d("File Helper: " + TAG, "Path: " + list.get(i));
    }


}

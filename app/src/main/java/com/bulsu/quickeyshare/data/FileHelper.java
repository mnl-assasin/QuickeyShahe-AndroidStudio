package com.bulsu.quickeyshare.data;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.bulsu.quickeyshare.model.FileItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class FileHelper {
    public static final String TAG = "FileHelper";

    public static String getFileLocation(String path) {
        return path.substring(0, path.lastIndexOf("/"));
    }

    public static String getFilename(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

    public static String getFileExtension(String path) {
        String filenameArray[] = path.split("\\.");
        String extension = filenameArray[filenameArray.length - 1];
        return extension;
    }

    public static double getSize(String path) {
        File file = new File(path);
        return Double.parseDouble(String.valueOf(file.length() / 1024));

    }

    public static double getAllSize(List<String> path) {
        double size = 0;

        for (int ctr = 0; ctr < path.size(); ctr++) {
            size += getSize(path.get(ctr));

        }

        return size;
    }

    public static String getFileSize(List<String> path) {
        int MB = 1024;
        int GB = MB * MB;

        double size = getAllSize(path);
        double fSize = size;
//        return String.valueOf(size);
        if (size < 1) {
            return size * 1000 + " Bytes";
        } else if (size > 1 && size < MB)
            return size + " KB";
        else if (size > MB && size < GB)
            return String.format(Locale.getDefault(), "%.2f", fSize / MB) + " MB";
        else
            return (size / 1000000 + String.valueOf(size % 1000000).substring(0, 2)) + "GB";
//        return "";
    }

    public static boolean isBelowLimit(List<String> path) {
        Log.d(TAG, "Selected size: " + path.size());
        Log.d(TAG, "Size: " + getAllSize(path));
        return getAllSize(path) < (500 * 1024);
    }

    public static String getFileSize(String path) {
        int MB = 1024;
        int GB = MB * MB;

        double size = getSize(path);
        double fSize = size;
//        return String.valueOf(size);
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
        logList("BaseFileChooserFragment", list);
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
        logList("BaseFileChooserFragment", list);
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
        logList("BaseFileChooserFragment", list);
        return list;
    }

    public static void logList(String TAG, List<String> list) {
        for (int i = 0; i < list.size(); i++)
            Log.d("File Helper: " + TAG, "Path: " + list.get(i));
    }

    public static ArrayList<FileItem> populateList(List<String> allPath, int category) {

        ArrayList<FileItem> items = new ArrayList<>();
        for (int ctr = 0; ctr < allPath.size(); ctr++) {
            items.add(new FileItem(allPath.get(ctr), category, false));
        }

        return items;
    }

    public static String getVideoDuration(Context ctx, String path) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            Log.d(TAG, "Path: " + path);
            retriever.setDataSource(ctx, Uri.parse(path));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long millis = Long.parseLong(time);

            return String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
        } catch (Exception e) {
            return "00:00:00";
        }
    }


}

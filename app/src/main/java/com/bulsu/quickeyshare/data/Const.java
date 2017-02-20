package com.bulsu.quickeyshare.data;

import android.os.Environment;

import java.io.File;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class Const {

    public static String FILE_FOLDER = "QuickeyShare";
    public static String COMPRESS_ZIP = "compress.zip";
    public static String ERROR_MESSAGE = "";
    public static String DEFAULT_FOLDER_PATH = Environment.getExternalStorageDirectory() + File.separator + Const.FILE_FOLDER;
    public static String DEFAULT_ZIP_PATH = DEFAULT_FOLDER_PATH+ File.separator + COMPRESS_ZIP;
}

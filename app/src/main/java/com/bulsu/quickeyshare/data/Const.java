package com.bulsu.quickeyshare.data;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mykelneds on 18/02/2017.
 */

public class Const {

    public static String FILE_FOLDER = "QuickeyShare";
    public static String COMPRESS_ZIP = "compress.zip";
    public static String ERROR_MESSAGE = "";
    public static String DEFAULT_FOLDER_PATH = Environment.getExternalStorageDirectory() + File.separator + Const.FILE_FOLDER;
    public static String DEFAULT_ZIP_PATH = DEFAULT_FOLDER_PATH + File.separator + COMPRESS_ZIP;

    public static String VAULT = ".vault";
    public static String DEFAULT_VAULT_PATH = Environment.getExternalStorageDirectory() + File.separator + ".QuickeyShareVault" + File.separator + VAULT;

    public static String VAULT_UNLOCKED = "vault";
    public static String DEFAULT_VAULT_UNLOCKED_PATH = Environment.getExternalStorageDirectory() + File.separator + "QuickeyShareUnlock" + File.separator + VAULT_UNLOCKED;

    public static final int CAT_FILE = 0;
    public static final int CAT_VIDEO = 1;
    public static final int CAT_IMAGE = 2;
    public static final int CAT_AUDIO = 3;

    public static List<String> selectedPath = new ArrayList<>();
}

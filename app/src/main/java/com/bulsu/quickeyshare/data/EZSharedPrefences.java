package com.bulsu.quickeyshare.data;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * Created by mykelneds on 12/02/2017.
 */

public class EZSharedPrefences {

    private static final String USER_PREFERENCES = "QuickeyShare_Prefs";


    static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setManifestReadStorage(Context ctx, boolean status) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putBoolean(Manifest.permission.READ_EXTERNAL_STORAGE, status);
        editor.apply();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void setManifestWriteStorage(Context ctx, boolean status) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, status);
        editor.apply();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean getManifestReadStorage(Context ctx) {
        return getSharedPref(ctx).getBoolean(Manifest.permission.READ_EXTERNAL_STORAGE, false);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean getManifestWriteStorage(Context ctx) {
        return getSharedPref(ctx).getBoolean(Manifest.permission.WRITE_EXTERNAL_STORAGE, false);
    }
}

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
    private static final String KEY_SECRET = "secretKey";
    private static final String KEY_PINCODE = "pinCode";
    private static final String KEY_SEC_QUESTION = "securityQuestion";
    private static final String KEY_SEC_ANSWER = "securityAnswer";

    private static SharedPreferences getSharedPref(Context ctx) {
        return ctx.getSharedPreferences(USER_PREFERENCES, Context.MODE_PRIVATE);
    }

    /**
     * S E T T E R
     */

    public static void setSecAnswer(Context ctx, String secret) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putString(KEY_SEC_ANSWER, secret);
        editor.apply();
    }


    public static void setSecQuestion(Context ctx, String secret) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putString(KEY_SEC_QUESTION, secret);
        editor.apply();
    }


    public static void setSecret(Context ctx, String secret) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putString(KEY_SECRET, secret);
        editor.apply();
    }

    public static void setPinCode(Context ctx, String pinCode) {
        SharedPreferences.Editor editor = getSharedPref(ctx).edit();
        editor.putString(KEY_PINCODE, pinCode);
        editor.apply();
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

    /**
     * G E T T E R
     */

    public static String getSecAnswer(Context ctx) {
        return getSharedPref(ctx).getString(KEY_SEC_ANSWER, "");
    }

    public static String getSecQuestion(Context ctx) {
        return getSharedPref(ctx).getString(KEY_SEC_QUESTION, "");
    }

    public static String getSecret(Context ctx) {
        return getSharedPref(ctx).getString(KEY_SECRET, "");
    }

    public static String getPinCode(Context ctx) {
        return getSharedPref(ctx).getString(KEY_PINCODE, "");
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

package com.bulsu.quickeyshare.builder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by mykelneds on 09/03/2017.
 */

public class DialogBuilder {

    public static void createDialog(Context ctx, String title, String message, String positiveBtn, DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveBtn, listener);
        builder.show();
    }

    public static void createDialog(Context ctx, String title, String message, String positiveBtn, DialogInterface.OnClickListener positiveListener, String negativeBtn, DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveBtn, positiveListener);
        builder.setNegativeButton(negativeBtn, negativeListener);
        builder.show();
    }
}

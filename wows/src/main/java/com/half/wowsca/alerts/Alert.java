package com.half.wowsca.alerts;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;

import com.half.wowsca.R;

/**
 * Created by Obsidian47 on 3/7/14.
 */
public class Alert {

    public static AlertDialog createGeneralAlert(Context ctx, String title, String message, String neutralText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setNegativeButton(neutralText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.show();
    }

    public static AlertDialog createGeneralAlert(Context ctx, String title, String message, String neutralText, int drawable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(drawable);
        builder.setNegativeButton(neutralText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.show();
    }

    public static AlertDialog createGeneralAlert(Context ctx, String title, String message, String neutralText, Drawable drawable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setIcon(drawable);
        builder.setNegativeButton(neutralText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        return builder.show();
    }

    public static AlertDialog generalNoInternetDialogAlert(final Activity ctx, String title, String message, String neutralText) {
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(ctx);
        alt_bld.setTitle(title);
        alt_bld.setMessage(message);
        alt_bld.setCancelable(true);
        alt_bld.setPositiveButton(neutralText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_SETTINGS);
                ctx.startActivity(intent);
            }
        });
        return alt_bld.show();
    }
}

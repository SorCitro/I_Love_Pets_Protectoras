package com.example.ilpp.classes;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.ilpp.BuildConfig;

public class Message {

    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void confirm(Context context, String message, AlertDialog.OnClickListener onYes) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setPositiveButton(context.getString(android.R.string.yes), onYes);
        builder.setNegativeButton(context.getString(android.R.string.no), null);
        builder.show();
    }

    public static void exception(Context context, Throwable e, String message) {
        String msg = BuildConfig.DEBUG
                ? String.format("%s: %s", message, e.getMessage())
                : message;
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}

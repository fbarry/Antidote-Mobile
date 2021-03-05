package com.example.antidote_mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.Nullable;

public class Utilities {

    public static String getRandomString(int len) {
        StringBuilder sb = new StringBuilder();
        while (len-- > 0) {
            sb.append((char) ('A' + (int) (Math.random() * 26)));
        }
        return sb.toString();
    }

    public static int getRandomInt(int low, int high) {
        int diff = high - low;
        int add = (int) (Math.random() * (diff + 1));
        return low + add;
    }

    public static void showConfirmationAlert(Activity activity,
                                             String title,
                                             String message,
                                             DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton(R.string.ok, listener)
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showInformationAlert(Activity activity,
                                            int title,
                                            int message,
                                            @Nullable DialogInterface.OnClickListener action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message);
        builder.setNegativeButton(R.string.ok, action == null ? (dialog, which) -> dialog.dismiss() : action);

        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showTwoPromptAlert(Activity activity,
                                     int title,
                                     int message,
                                     int prompt1,
                                     int prompt2,
                                     DialogInterface.OnClickListener prompt1Action,
                                     DialogInterface.OnClickListener prompt2Action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton(prompt1, prompt1Action)
                .setNegativeButton(prompt2, prompt2Action);

        AlertDialog alert = builder.create();
        alert.show();
    }
}

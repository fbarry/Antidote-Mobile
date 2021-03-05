package com.example.antidote_mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

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
}

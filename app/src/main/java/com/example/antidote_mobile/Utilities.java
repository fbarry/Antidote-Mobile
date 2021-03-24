package com.example.antidote_mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import java.util.Random;

public class Utilities {

    static String[] adj = new String[]{"cute", "mad", "big", "small", "sad", "bad", "dull", "dizzy",
            "crazy", "busy", "calm", "blue", "red", "pink", "teal", "fair",
            "evil", "fine", "good", "ill", "hurt", "kind", "tiny"};
    static String[] noun = new String[]{"bug", "art", "food", "data", "law", "bird", "love", "fact",
            "hat", "idea", "oven", "bulb", "dino", "army", "user", "road",
            "mole", "math", "lad", "wood", "cell", "mood", "ad", "debt"};

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

    public static String getRandomNumberString(int len) {
        StringBuilder sb = new StringBuilder();
        while (len-- > 0) {
            sb.append((char) ('0' + (int) (Math.random() * 10)));
        }
        return sb.toString();
    }

    public static String getRandomGuestUsername() {
        Random rand = new Random();
        String ret = adj[rand.nextInt(adj.length)] + "_" + noun[rand.nextInt(noun.length)] + "_";
        ret += getRandomNumberString(AntidoteMobile.maxUsernameLength - ret.length());
        return ret;
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

    public static void setDrawableColor(String colorString, Drawable img) {
        int iColor = Color.parseColor(colorString);

        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;

        float[] matrix = {0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0};

        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        img.setColorFilter(colorFilter);
    }

    public static int getNumberResource(int number) {
        switch (number) {
            case 0:
                return R.drawable.number0;
            case 1:
                return R.drawable.number1;
            case 2:
                return R.drawable.number2;
            case 3:
                return R.drawable.number3;
            case 4:
                return R.drawable.number4;
            case 5:
                return R.drawable.number5;
            case 6:
                return R.drawable.number6;
            case 7:
                return R.drawable.number7;
        }
        return -1;
    }

    public static double dist(int x1, int y1, int x2, int y2) {
        int dx = x1 - x2;
        int dy = y1 - y2;
        return Math.sqrt(dx * dx + dy * dy);
    }
}

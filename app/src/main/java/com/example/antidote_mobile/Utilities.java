package com.example.antidote_mobile;

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
        int add = (int)(Math.random() * (diff + 1));
        return low + add;
    }
}

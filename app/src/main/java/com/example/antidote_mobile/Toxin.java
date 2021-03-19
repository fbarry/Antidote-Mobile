package com.example.antidote_mobile;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.core.content.res.ResourcesCompat;

@SuppressWarnings("unused")
public enum Toxin {
    SERUMN("SERUM-N", R.drawable.serum, R.drawable.serumx),
    OSLERSOIL("OSLER'S OIL", R.drawable.ostersoil, R.drawable.ostersoilx),
    RUBIMAXB("RUBIMAXB", R.drawable.rubiximab, R.drawable.rubiximabx),
    C9TONIC("C9-TONIC", R.drawable.c9tonic, R.drawable.c9tonicx),
    BOOTHEIDE("BOOTHEIDE", R.drawable.bootheide, R.drawable.bootheidex),
    W2ROSE("W²ROSE", R.drawable.w2rose, R.drawable.w2rosex),
    MXVILE("MX-VILE", R.drawable.mxvile, R.drawable.mxvilex),
    AGENTU("AGENT-U", R.drawable.agentu, R.drawable.agentux),
    NONE("NONE", -1, -1);

    private final String text;
    private final int res;
    private final int resx;

    Toxin(String s, int r, int rx) {
        this.text = s;
        this.res = r;
        resx = rx;
    }

    public String getText() {
        return text;
    }

    public int getResX() {
        return resx;
    }

    public int getRes() {
        return res;
    }

    public static Toxin fromString(String text) {
        if (text == null) return null;
        for (Toxin t : Toxin.values()) {
            if (t.text.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return NONE;
    }

}

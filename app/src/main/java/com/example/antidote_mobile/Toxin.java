package com.example.antidote_mobile;

public enum Toxin {
    SERUMN("SERUM-N", R.drawable.serum, R.drawable.serumx, "#4e4662", R.drawable.serumthumbnail),
    OSLERSOIL("OSLER'S OIL", R.drawable.ostersoil, R.drawable.ostersoilx, "#729f7a", R.drawable.osleroilthumbnail),
    RUBIMAXB("RUBIMAXB", R.drawable.rubiximab, R.drawable.rubiximabx, "#dc5e38", R.drawable.rubiximabthumbnail),
    C9TONIC("C9-TONIC", R.drawable.c9tonic, R.drawable.c9tonicx, "#718f40", R.drawable.c9tonicthumbnail),
    BOOTHEIDE("BOOTHEIDE", R.drawable.bootheide, R.drawable.bootheidex, "#7f4e79", R.drawable.bootheidethumbnail),
    W2ROSE("W²ROSE", R.drawable.w2rose, R.drawable.w2rosex, "#b3486e", R.drawable.w2rosethumbnail),
    MXVILE("MX-VILE", R.drawable.mxvile, R.drawable.mxvilex, "#b9c656", R.drawable.mxvilethumbnail),
    AGENTU("AGENT-U", R.drawable.agentu, R.drawable.agentux, "#e8833a", R.drawable.agentuthumbnail),
    NONE("NONE", -1, -1, "#ffffff", -1);

    private final String text, colorString;
    private final int res, resx, thumbres;

    Toxin(String s, int r, int rx, String cs, int tr) {
        this.text = s;
        this.res = r;
        resx = rx;
        colorString = cs;
        thumbres = tr;
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

    public String getColorString() {
        return colorString;
    }

    public int getThumbres() {
        return thumbres;
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

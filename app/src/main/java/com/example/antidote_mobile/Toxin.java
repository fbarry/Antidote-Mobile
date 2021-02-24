package com.example.antidote_mobile;

@SuppressWarnings("unused")
public enum Toxin {
    SERUMN("SERUM-N"),
    OSLERSOIL("OSLER'S OIL"),
    RUBIMAXB("RUBIMAXB"),
    C9TONIC("C9-TONIC"),
    BOOTHEIDE("BOOTHEIDE"),
    W2ROSE("W²ROSE"),
    MXVILE("MX-VILE"),
    AGENTU("AGENT-U"),
    NONE("NONE");

    private final String text;

    Toxin(String s) {
        this.text = s;
    }

    public String getText() {
        return text;
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

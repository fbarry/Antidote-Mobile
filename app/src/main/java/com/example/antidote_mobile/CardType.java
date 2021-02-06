package com.example.antidote_mobile;

public enum CardType {
    TOXIN("TOXIN"),
    SYRINGE("SYRINGE"),
    ANTIDOTE("ANTIDOTE"),
    NONE("NONE");


    private String text;

    CardType(String s) {
        this.text = s;
    }

    public String getText() {
        return text;
    }

    public static CardType fromString(String text) {
        for (CardType t : CardType.values()) {
            if (t.text.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return NONE;
    }
}

package com.example.antidote_mobile;

public enum CardType {
    TOXIN("TOXIN"),
    SYRINGE("SYRINGE"),
    ANTIDOTE("ANTIDOTE"),
    NONE("NONE");


    private final String text;

    CardType(String s) {
        this.text = s;
    }

    @SuppressWarnings("unused")
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

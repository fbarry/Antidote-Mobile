package com.example.antidote_mobile;

@SuppressWarnings("unused")
public enum ActionType {
    PASSLEFT("Pass Left"),
    PASSRIGHT("Pass Right"),
    TRADE("Trade"),
    DISCARD("Discard"),
    SYRINGE("Syringe"),
    NONE("NONE");

    private final String text;

    ActionType(String s) {
        this.text = s;
    }

    public String getText() {
        return text;
    }

    public static ActionType fromString(String text) {
        if (text == null) return null;
        for (ActionType t : ActionType.values()) {
            if (t.text.equalsIgnoreCase(text)) {
                return t;
            }
        }
        return NONE;
    }

}

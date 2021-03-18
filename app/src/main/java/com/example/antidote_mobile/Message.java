package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USERNAME_KEY = "username";
    public static final String BODY_KEY = "body";
    public static final String GAME_KEY = "gameId";

    public String getUsername() {
        return getString(USERNAME_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public void setUsername(String userId) {
        put(USERNAME_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setGame(String gameId) {
        put(GAME_KEY, gameId);
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            return ((Message) o).getObjectId().equals(getObjectId());
        } else {
            return false;
        }
    }

}

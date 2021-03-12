package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String USER_ID_KEY = "userId";
    public static final String BODY_KEY = "body";
    public static final String GAME_KEY = "gameId";

    public String getUserId() {
        return getString(USER_ID_KEY);
    }

    public String getBody() {
        return getString(BODY_KEY);
    }

    public String getGameId() { return getString(GAME_KEY); }

    public void setUserId(String userId) {
        put(USER_ID_KEY, userId);
    }

    public void setBody(String body) {
        put(BODY_KEY, body);
    }

    public void setGame(String gameId) { put(GAME_KEY, gameId); }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            return ((Message)o).getObjectId().equals(getObjectId());
        } else {
            return false;
        }
    }

}

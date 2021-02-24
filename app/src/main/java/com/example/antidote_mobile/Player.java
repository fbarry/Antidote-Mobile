package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@ParseClassName("Player")
public class Player extends ParseObject implements Serializable {

    public Player() {

    }

    public String who() {
        return this.getString("who");
    }

    public ArrayList<String> cards() {
        //noinspection unchecked
        return (ArrayList<String>) this.get("cards");
    }

    public int points() {
        return this.getInt("Points");
    }

    public void setWho(String who) {
        this.put("who", who);
    }

    public void setCards(List<String> cards) {
        this.put("cards", cards);
    }

    public void setPoints(int points) {
        this.put("points", points);
    }

    public static Player createPlayer(User user) {
        if (user.getObjectId() == null) {
            User signedup = User.signUpGuest(user.getUsername(), AntidoteMobile.guestPassword);

            if (signedup == null) {
                System.out.println("FAILED TO CREATE SIGN UP");
                return null;
            }

            user.setObjectId(signedup.getObjectId());
            System.out.println("SIGNED UP: " + user.getObjectId());
        }
        Player ret = new Player();

        ret.setWho(user.getObjectId());
        ret.setCards(new ArrayList<>());
        ret.setPoints(0);

        try {
            ret.save();
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

}

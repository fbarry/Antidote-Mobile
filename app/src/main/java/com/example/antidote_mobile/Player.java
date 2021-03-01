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

    public static final String guestUsername = "IM_A_GUEST_aniveacuinveowihoaiscvmeoin";

    public Player() {

    }

    public String who() {
        return this.getString("who");
    }

    public ArrayList<String> cards() {
        //noinspection unchecked
        return (ArrayList<String>) this.get("cards");
    }

    public String username() {
        return this.getString("username");
    }

    public void setUsername(String username) {
        this.put("username", username);
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

            try {
                user.signUp();
            } catch (ParseException e) {
                e.printStackTrace();
                System.out.println("Failed to sign user up!");
            }

            System.out.println("SIGNED UP: " + user.getObjectId());
        }
        Player ret = new Player();

        ret.setWho(user.getObjectId());
        ret.setCards(new ArrayList<>());
        ret.setPoints(0);

        if(user.isGuest()) ret.setUsername(Player.guestUsername);
        else ret.setUsername(user.getUsername());

        try {
            ret.save();
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

}

package com.example.antidote_mobile;

import androidx.annotation.NonNull;

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

    @Override
    public void put(@NonNull String key, @NonNull Object value) {
        super.put(key, value);
        saveInBackground();
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            Player p = (Player) o;
            return getObjectId().equals(p.getObjectId());
        }
        return false;
    }

    public int getDifficulty() {
        return this.getInt("difficulty");
    }

    public void setDifficulty(int difficulty) {
        this.put("difficulty", difficulty);
    }

    public boolean isAI() {
        return this.getBoolean("AI");
    }

    public void setIsAI(boolean isAI) {
        this.put("AI", isAI);
    }

    public boolean isHost() {
        return this.getBoolean("isHost");
    }

    public boolean isLocked() {
        return this.getBoolean("isLocked");
    }

    public void setIsLocked(boolean isLocked) {
        this.put("isLocked", isLocked);
    }

    public int selectedIdx() {
        return this.getInt("selectedIdx");
    }

    public void setSelectedIdx(int selectedIdx) {
        this.put("selectedIdx", selectedIdx);
    }

    public void deselect() {
        setIsLocked(false);
        setSelectedIdx(-1);
    }

    public String who() {
        return this.getString("who");
    }

    public ArrayList<String> cards() {
        //noinspection unchecked
        return (ArrayList<String>) this.get("cards");
    }

    public ArrayList<String> workstation() {
        //noinspection unchecked
        return (ArrayList<String>) this.get("workstation");
    }

    public void setWorkstation(List<String> workstation) {
        this.put("workstation", workstation);
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

    public void setIsHost(boolean isHost) {
        this.put("isHost", isHost);
    }

    public void setCards(List<String> cards) {
        this.put("cards", cards);
    }

    public void setPoints(int points) {
        this.put("points", points);
    }

    public boolean hasSyringe() {
        return cards().contains(CardType.SYRINGE.getText());
    }

    public static Player createPlayer(User user, boolean isHost) {
        Player ret = new Player();

        ret.setIsHost(isHost);
        ret.setWho(user.getObjectId());
        ret.setCards(new ArrayList<>());
        ret.setWorkstation(new ArrayList<>());
        ret.setPoints(0);
        ret.setIsLocked(false);
        ret.setSelectedIdx(-1);
        ret.setIsAI(false);

        if (user.isGuest()) ret.setUsername(Utilities.getRandomGuestUsername());
        else ret.setUsername(user.getUsername());

        if (isHost) ret.setUsername(ret.username() + " (Host)");

        try {
            ret.save();
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

}

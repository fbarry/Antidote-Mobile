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

    @Override
    public boolean equals(Object o) {
        if (o.getClass().equals(getClass())) {
            Player p = (Player) o;
            return getObjectId().equals(p.getObjectId());
        }
        return false;
    }

    public PlayerAI.Difficulty difficulty() {
        return PlayerAI.Difficulty.fromDiffVal(this.getInt("difficulty"));
    }

    public void setDifficulty(PlayerAI.Difficulty difficulty) {
        this.put("difficulty", difficulty.getDiffVal());
    }

    public ArrayList<String> memory() {
        //noinspection unchecked
        return (ArrayList<String>) get("memory");
    }

    public void setMemory(List<String> memory) {
        put("memory", memory);
    }

    public void addMemory(String memory) {
        ArrayList<String> memories = memory();
        memories.add(memory);
        setMemory(memories);
    }

    public boolean needsGameOverScreen() {
        return getBoolean("needsGameOverScreen");
    }

    public void setNeedsGameOverScreen(boolean needsGameOverScreen) {
        put("needsGameOverScreen", needsGameOverScreen);
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

    public int calculatePoints(Toxin gameToxin) {
        if (Card.getCardType(cards().get(0)) == CardType.ANTIDOTE) {
            int value = Card.getNumber(cards().get(0));
            if (Card.getToxin(cards().get(0)) == gameToxin) {
                return value;
            }
            return -value;
        }
        return -1;
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
        return this.getInt("points");
    }

    public int lastRoundPoints() {
        return this.getInt("lastRoundPoints");
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

    public void setLastRoundPoints(int lastRoundPoints) {
        this.put("lastRoundPoints", lastRoundPoints);
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
        ret.setMemory(new ArrayList<>());

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

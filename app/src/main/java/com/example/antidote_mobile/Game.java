package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;

public class Game implements Serializable {
    public String joinCode, objectId;
    public ArrayList<String> playerIds;
    public int numPlayers, currentTurn, numCards, numRoundsCompleted;
    public Toxin toxin;

    public Game() {
        this.playerIds = new ArrayList<>();
    }

    public Game(ParseObject po) {
        this.currentTurn = po.getInt("currentTurn");
        this.numCards = po.getInt("numCards");
        this.numRoundsCompleted = po.getInt("numRoundsCompleted");
        this.numPlayers = po.getInt("numPlayers");
        this.toxin = Toxin.fromString(po.getString("toxin"));

        //noinspection unchecked
        this.playerIds = (ArrayList<String>) po.get("players");

        this.joinCode = po.getString("joinCode");
        this.objectId = po.getObjectId();
    }

    public static Game getGame(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        try {
            ParseObject po = query.get(objectId);
            return new Game(po);
        } catch (ParseException e) {
            return null;
        }
    }

    // This function will create a game and add the player to the game then return the game object
    public static Game createGame(Player player) {
        return null;
    }

    public static Game joinGame(String joinCode, Player player) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("joinCode", joinCode);

        ParseObject po = null;

        try {
            ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
            for (ParseObject obj : candidates) {
                if (obj.getString("joinCode").equals(joinCode)) {
                    po = obj;
                    break;
                }
            }
        } catch (ParseException e) {
            return null;
        }
        if (po == null) return null;

        //noinspection unchecked
        ArrayList<String> ids = (ArrayList<String>) po.get("players");

        ids.add(player.objectId);
        po.put("players", ids);
        po.put("numPlayers", ids.size());

        po.saveInBackground();

        return new Game(po);
    }

    public void update() throws ParseException {
        ParseQuery<ParseObject> query = new ParseQuery<>("Game");
        ParseObject po = query.get(this.objectId);

        this.currentTurn = po.getInt("currentTurn");
        this.numCards = po.getInt("numCards");
        this.numRoundsCompleted = po.getInt("numRoundsCompleted");
        this.numPlayers = po.getInt("numPlayers");
        this.toxin = Toxin.fromString(po.getString("toxin"));

        //noinspection unchecked
        this.playerIds = (ArrayList<String>) po.get("players");

        this.joinCode = po.getString("joinCode");
        this.objectId = po.getObjectId();
    }

}

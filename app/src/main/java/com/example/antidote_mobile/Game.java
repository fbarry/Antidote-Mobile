package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class Game implements Serializable {
    public String roomCode, objectId, host;
    public ArrayList<String> players;
    public int numPlayers, currentTurn, numCards, numRoundsCompleted;
    public Toxin toxin;

    public Game() {
        this.players = new ArrayList<>();
    }

    public Game(ParseObject po) {
        this.host = po.getString("host");
        this.currentTurn = po.getInt("currentTurn");
        this.numCards = po.getInt("numCards");
        this.numRoundsCompleted = po.getInt("numRoundsCompleted");
        this.numPlayers = po.getInt("numPlayers");
        this.toxin = Toxin.fromString(po.getString("toxin"));

        //noinspection unchecked
        this.players = (ArrayList<String>) po.get("players");

        this.roomCode = po.getString("roomCode");
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

    public static Game createGame(Player player) {
        String newGameCode = Utilities.getRandomString(AntidoteMobile.gameCodeLength);

        ParseObject newGame = new ParseObject("Game");

        newGame.put("host", player.objectId);
        newGame.put("roomCode", newGameCode);
        newGame.put("numPlayers", 1);
        newGame.put("currentTurn", 0);
        newGame.put("numCards", -1);
        newGame.put("players", new ArrayList<>(Collections.singletonList(player.objectId)));
        newGame.put("numRoundsCompleted", 0);
        newGame.put("toxin", Toxin.NONE.getText());

        try {
            newGame.save();
            return new Game(newGame);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static Game rejoinGame(Player player) {
        // Theoretically, this player SHOULD be in some game if we found it
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        ArrayList<ParseObject> games;
        try {
            games = (ArrayList<ParseObject>) query.find();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        if (games == null) return null;
        for (ParseObject gameCandidate : games) {
            List<Object> players = gameCandidate.getList("players");
            if (players.contains(player.objectId)) {
                return new Game(gameCandidate);
            }
        }

        return null;
    }

    public static Game joinGame(String roomCode, Player player) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("roomCode", roomCode);

        ParseObject po = null;

        try {
            ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
            System.out.println(candidates);
            for (ParseObject obj : candidates) {
                if (obj.getString("roomCode").equals(roomCode)) {
                    po = obj;
                    break;
                }
            }
        } catch (ParseException e) {
            System.out.println("Could not find game object");
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
        this.players = (ArrayList<String>) po.get("players");

        this.roomCode = po.getString("roomCode");
        this.objectId = po.getObjectId();
    }

}

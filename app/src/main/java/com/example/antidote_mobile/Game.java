package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
@ParseClassName("Game")
public class Game extends ParseObject implements Serializable {

    public Game() {

    }

    public String roomCode() {
        return getString("roomCode");
    }

    public void setRoomCode(String roomCode) {
        put("roomCode", roomCode);
    }

    public String host() {
        return getString("host");
    }

    public void setHost(String host) {
        put("host", host);
    }

    public ArrayList<String> players() {
        //noinspection unchecked
        return (ArrayList<String>) get("players");
    }

    public void setPlayers(ArrayList<String> players) {
        put("players", players);
    }

    public int numPlayers() {
        return getInt("numPlayers");
    }

    public void setNumPlayers(int numPlayers) {
        put("numPlayers", numPlayers);
    }

    public int currentTurn() {
        return getInt("currentTurn");
    }

    public void setCurrentTurn(int currentTurn) {
        put("currentTurn", currentTurn);
    }

    public void incrementCurrentTurn() {
        put("currentTurn", (currentTurn() + 1) % numPlayers());
    }

    public int numCards() {
        return getInt("numCards");
    }

    public void setNumCards(int numCards) {
        put("numCards", numCards);
    }

    public int numRoundsCompleted() {
        return getInt("numRoundsCompleted");
    }

    public void setNumRoundsCompleted(int numRoundsCompleted) {
        put("numRoundsCompleted", numRoundsCompleted);
    }

    public Toxin toxin() {
        return Toxin.fromString(getString("toxin"));
    }

    public void setToxin(Toxin toxin) {
        put("toxin", toxin.getText());
    }

    public void setToxin(String toxin) {
        put("toxin", toxin);
    }

    public void addPlayer(String newPlayer) {
        ArrayList<String> curPlayers = players();
        curPlayers.add(newPlayer);
        setPlayers(curPlayers);
        setNumPlayers(curPlayers.size());
    }

    public void addPlayer(Player newPlayer) {
        addPlayer(newPlayer.getObjectId());
    }

    public static Game getGame(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        try {
            return (Game) query.get(objectId);
        } catch (ParseException e) {
            return null;
        }
    }

    public static Game createGame(Player player) {
        String newGameCode = Utilities.getRandomString(AntidoteMobile.gameCodeLength);

        Game newGame = new Game();

        newGame.setHost(player.getObjectId());
        newGame.setRoomCode(newGameCode);
        newGame.setNumPlayers(1);
        newGame.setCurrentTurn(0);
        newGame.setNumCards(-1);
        newGame.setPlayers(new ArrayList<>(Collections.singletonList(player.getObjectId())));
        newGame.setNumRoundsCompleted(0);
        newGame.setToxin(Toxin.NONE.getText());

        try {
            newGame.save();
            return newGame;
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
            if (players != null && players.contains(player.getObjectId())) {
                return (Game) gameCandidate;
            }
        }

        return null;
    }

    public static Game joinGame(String roomCode, Player player) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.whereEqualTo("roomCode", roomCode);

        Game toJoin;

        try {
            toJoin = (Game) query.getFirst();
        } catch (ParseException e) {
            System.out.println("Could not find game object");
            return null;
        }

        if (toJoin == null) return null;

        toJoin.addPlayer(player);
        toJoin.saveInBackground();

        return toJoin;
    }

    public void update() throws ParseException {
        ParseQuery<ParseObject> query = new ParseQuery<>("Game");
        Game po = (Game) query.get(getObjectId());

        setCurrentTurn(po.currentTurn());
        setNumCards(po.numCards());
        setNumRoundsCompleted(po.numRoundsCompleted());
        setNumPlayers(po.numPlayers());
        setPlayers(po.players());
        setRoomCode(po.roomCode());
        setObjectId(po.getObjectId());
        setToxin(po.toxin());
        setHost(po.host());
        setCurrentTurn(po.currentTurn());
    }

    // TODO: Handle errors
    @SuppressWarnings("StatementWithEmptyBody")
    public void deleteGame() {
        ArrayList<String> playerList = new ArrayList<>(players());
        for (String playerId : playerList) removePlayer(playerId);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(getObjectId(), (object, e) -> {
            if (e == null) {
                object.deleteInBackground(e2 -> {
                    if (e2 == null) {
                    } else {
                    }
                });
            } else {
            }
        });
    }

    // TODO: Handle errors
    @SuppressWarnings("StatementWithEmptyBody")
    public void removePlayer(String playerId) {
        ArrayList<String> curPlayers = players();
        curPlayers.remove(playerId);
        setPlayers(curPlayers);
        setNumPlayers(curPlayers.size());

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");

        query.getInBackground(playerId, (object, e) -> {
            if (e == null) {
                object.deleteInBackground(e2 -> {
                    if (e2 == null) {
                        ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Game");

                        ParseObject po;
                        try {
                            po = query2.get(getObjectId());

                            //noinspection unchecked
                            ArrayList<String> ids = (ArrayList<String>) po.get("players");

                            assert ids != null;
                            ids.remove(playerId);
                            po.put("players", ids);
                            po.put("numPlayers", ids.size());

                            po.saveInBackground();

                        } catch (ParseException e3) {
                            e3.printStackTrace();
                        }
                    } else {
                    }
                });
            } else {
            }
        });
    }

}
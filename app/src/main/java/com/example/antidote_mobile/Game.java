package com.example.antidote_mobile;

import androidx.annotation.NonNull;

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

    @Override
    public void put(@NonNull String key, @NonNull Object value) {
        super.put(key, value);
        saveInBackground();
    }

    public String currentAction() {
        return getString("currentAction");
    }

    public void setCurrentAction(String currentAction) {
        put("currentAction", currentAction);
    }

    public boolean isPrivate() {
        return getBoolean("private");
    }

    public void setPrivate(boolean isPrivate) {
        put("private", isPrivate);
    }

    public ActionType currentActionType() {
        return ActionType.fromString(currentAction());
    }

    public void setCurrentAction(ActionType currentAction) {
        setCurrentAction(currentAction.getText());
    }

    public void setTradeTarget(int tradeTarget) {
        put("tradeTarget", tradeTarget);
    }

    public int tradeTarget() {
        return getInt("tradeTarget");
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

    public void initialize(Player currentPlayer) {
        setNumRoundsCompleted(0);
        setCurrentTurn(Utilities.getRandomInt(0, players().size() - 1));

        int numFormulas;
        if (numPlayers() == 7) numFormulas = 8;
        else numFormulas = 7;

        int numCardsPerFormula;
        if (numPlayers() == 2) numCardsPerFormula = 3;
        else numCardsPerFormula = numPlayers();

        int numSyringes;
        if (numPlayers() == 2 || numPlayers() == 3) numSyringes = 3;
        else if (numPlayers() == 4) numSyringes = 2;
        else if (numPlayers() == 5) numSyringes = 4;
        else if (numPlayers() == 6) numSyringes = 6;
        else numSyringes = 7;

        int startingHandSize;
        if (4 <= numPlayers() && numPlayers() <= 6) startingHandSize = 9;
        else startingHandSize = 10;

        setNumCards(startingHandSize);

        int numSpecialDist;
        if (numPlayers() == 2 || numPlayers() == 3) numSpecialDist = 3;
        else numSpecialDist = 2;

        @SuppressWarnings("unchecked")
        ArrayList<String>[] hands = new ArrayList[numPlayers()];
        for (int player = 0; player < numPlayers(); ++player)
            hands[player] = new ArrayList<>();

        ArrayList<Card> specialPile = new ArrayList<>();
        for (int formula = 0; formula < numFormulas; ++formula) {
            Card add = new Card(0, 0);
            add.setCardData(CardType.TOXIN, Toxin.values()[formula]);
            specialPile.add(add);
        }

        setToxin(specialPile.remove(Utilities.getRandomInt(0, specialPile.size() - 1)).toxin);

        for (int syringe = 0; syringe < numSyringes; ++syringe) {
            Card add = new Card(0, 0);
            add.setCardData(CardType.SYRINGE);
            specialPile.add(add);
        }

        for (int player = 0; player < numPlayers(); ++player) {
            for (int dist = 0; dist < numSpecialDist; ++dist) {
                hands[player].add(specialPile.remove(Utilities.getRandomInt(0, specialPile.size() - 1)).getStringValue());
            }
        }

        ArrayList<Card> remainingCards = new ArrayList<>();
        for (int formula = 0; formula < numFormulas; ++formula) {
            for (int number = 1; number <= numCardsPerFormula; ++number) {
                Card add = new Card(0, 0);
                add.setCardData(CardType.ANTIDOTE, Toxin.values()[formula], number);
                remainingCards.add(add);
            }
        }

        for (int player = 0; player < numPlayers(); ++player) {
            for (int cardNum = numSpecialDist; cardNum < startingHandSize; ++cardNum) {
                hands[player].add(remainingCards.remove(Utilities.getRandomInt(0, remainingCards.size() - 1)).getStringValue());
            }
        }

        int playerIndex = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
        for (String playerId : players()) {
            if (playerId.equals(currentPlayer.getObjectId())) {
                currentPlayer.setCards(new ArrayList<>(hands[playerIndex]));
            }
            try {
                ParseObject player = query.get(playerId);
                player.put("cards", hands[playerIndex++]);
                player.saveInBackground();
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
        newGame.setCurrentAction(ActionType.NONE.getText());
        newGame.setPrivate(true);
        newGame.setTradeTarget(-1);

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
        if (toJoin.numCards() > 0) return toJoin;

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
        setTradeTarget(po.tradeTarget());
        setPrivate(po.isPrivate());
    }

    // TODO: Handle errors
    @SuppressWarnings("StatementWithEmptyBody")
    public void deleteGame() {
        deleteAllChats();

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

    public void deleteAllChats() {
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        query.whereEqualTo(Message.GAME_KEY, getObjectId());

        query.findInBackground((objects, e) -> {
            for (ParseObject o : objects) {
                o.deleteInBackground();
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
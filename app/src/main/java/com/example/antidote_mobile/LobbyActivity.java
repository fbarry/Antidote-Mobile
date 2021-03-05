package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Timer;

public class LobbyActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    public static final int millisPerUpdate = 4000;
    Game game;
    Player currentPlayer;
    Timer refreshTimer;
    RecyclerView playerList;
    PlayerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        playerList = findViewById(R.id.playerList);

        LinearLayoutManager llm = new LinearLayoutManager(this,
                                                            LinearLayoutManager.VERTICAL,
                                                false);
        playerList.setLayoutManager(llm);

        adapter = new PlayerAdapter(LobbyActivity.this, game, currentPlayer.isHost());
        playerList.setAdapter(adapter);

        if (!currentPlayer.isHost()) {
            Button startGameButton = findViewById(R.id.startGameButton);
            startGameButton.setVisibility(View.GONE);
            Button endGameButton = findViewById(R.id.endGameButton);
            endGameButton.setVisibility(View.GONE);
        } else {
            Button leaveGameButton = findViewById(R.id.leaveGameButton);
            leaveGameButton.setVisibility(View.GONE);
        }

        TextView roomCodeTextView = findViewById(R.id.roomCodeTextView);
        roomCodeTextView.setText(game.roomCode());

        updatePlayerList();

        refreshTimer = new Timer();

        update();
//        refreshTimer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                runOnUiThread(() -> update());
//            }
//        }, 0, millisPerUpdate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        refreshTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (currentPlayer != null && game != null && currentPlayer.isHost())
            game.deleteGame();
        else if (currentPlayer != null && game != null)
            game.removePlayer(currentPlayer.getObjectId());
    }

    public void lobbyRefresh(View v) {
        update();
    }

    public void update() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(game.getObjectId(), (object, e) -> {
            if (e != null) {
                if (object == null) {
                    game = null;
                    LobbyActivity.this.finish();
                }
            } else {
                game = (Game) object;
                adapter.setGame(game);
                updatePlayerList();
                updateGameScreen();
            }
        });
    }

    public void updateGameScreen() {
        if (game.numCards() > 0) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Player");

            try {
                ParseObject po = query.get(currentPlayer.getObjectId());
                currentPlayer = (Player) po;
                goToGameScreen();
            } catch (ParseException e) {
                Utilities.showInformationAlert(this,
                        R.string.could_not_connect,
                        R.string.check_your_internet,
                        (dialog, which) -> {
                            Button leave = findViewById(R.id.leaveGameButton);
                            leave.callOnClick();
                        });
                e.printStackTrace();
            }
        }
    }

    public void updatePlayerList() {
        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());

        getPlayers.findInBackground((objects, e) -> {
            boolean stillInTheGame = false;

            ArrayList<Player> list = new ArrayList<>();
            for (ParseObject po : objects) {
                System.out.println("Found player with objectid " + po.getObjectId());
                list.add((Player) po);
                if (po.getObjectId().equals(currentPlayer.getObjectId())) {
                    stillInTheGame = true;
                }
            }

            if (!stillInTheGame) {
                Utilities.showInformationAlert(this,
                        R.string.you_were_kicked,
                        R.string.sorry,
                        (dialog, which) -> {
                            currentPlayer = null;
                            LobbyActivity.this.finish();
                        });
            }

            ArrayList<Player> copy = new ArrayList<>(adapter.getPlayers());
            for (Player p : copy) {
                if (!list.contains(p)) adapter.removePlayer(p);
            } for (Player p : list) {
                if (!copy.contains(p)) adapter.addPlayer(p);
            }
        });
    }

    public void leaveGame(View v) {
        game.removePlayer(currentPlayer.getObjectId());
        currentPlayer = null;
        LobbyActivity.this.finish();
    }

    public void endGame(View v) {
        game.deleteGame();
        currentPlayer = null;
        game = null;
        LobbyActivity.this.finish();
    }

    public void startGame(View v) throws ParseException {
        if (game.numPlayers() < 2) return;

        game.setNumRoundsCompleted(0);
        game.setCurrentTurn(Utilities.getRandomInt(0, game.players().size() - 1));

        int numFormulas;
        if (game.numPlayers() == 7) numFormulas = 8;
        else numFormulas = 7;

        int numCardsPerFormula;
        if (game.numPlayers() == 2) numCardsPerFormula = 3;
        else numCardsPerFormula = game.numPlayers();

        int numSyringes;
        if (game.numPlayers() == 2 || game.numPlayers() == 3) numSyringes = 3;
        else if (game.numPlayers() == 4) numSyringes = 2;
        else if (game.numPlayers() == 5) numSyringes = 4;
        else if (game.numPlayers() == 6) numSyringes = 6;
        else numSyringes = 7;

        int startingHandSize;
        if (4 <= game.numPlayers() && game.numPlayers() <= 6) startingHandSize = 9;
        else startingHandSize = 10;

        game.setNumCards(startingHandSize);

        int numSpecialDist;
        if (game.numPlayers() == 2 || game.numPlayers() == 3) numSpecialDist = 3;
        else numSpecialDist = 2;

        @SuppressWarnings("unchecked")
        ArrayList<String>[] hands = new ArrayList[game.numPlayers()];
        for (int player = 0; player < game.numPlayers(); ++player)
            hands[player] = new ArrayList<>();

        ArrayList<Card> specialPile = new ArrayList<>();
        for (int formula = 0; formula < numFormulas; ++formula) {
            Card add = new Card(0, 0);
            add.setCardData(CardType.TOXIN, Toxin.values()[formula]);
            specialPile.add(add);
        }

        game.setToxin(specialPile.remove(Utilities.getRandomInt(0, specialPile.size() - 1)).toxin);

        for (int syringe = 0; syringe < numSyringes; ++syringe) {
            Card add = new Card(0, 0);
            add.setCardData(CardType.SYRINGE);
            specialPile.add(add);
        }

        for (int player = 0; player < game.numPlayers(); ++player) {
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

        for (int player = 0; player < game.numPlayers(); ++player) {
            for (int cardNum = numSpecialDist; cardNum < startingHandSize; ++cardNum) {
                hands[player].add(remainingCards.remove(Utilities.getRandomInt(0, remainingCards.size() - 1)).getStringValue());
            }
        }

        int playerIndex = 0;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
        for (String playerId : game.players()) {
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

        game.save();
    }

    public void goToGameScreen() {
        LobbyActivity.this.finish();

        Intent goToGame = new Intent(LobbyActivity.this, GameActivity.class);
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        sendGame.putSerializable("currentPlayer", currentPlayer);
        goToGame.putExtras(sendGame);
        startActivity(goToGame);
    }
}

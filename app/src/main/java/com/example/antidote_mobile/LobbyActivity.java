package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyActivity extends AppCompatActivity {

    public static final int millisPerUpdate = 4000;
    Game game;
    Player currentPlayer;
    Timer refreshTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        if (!currentPlayer.getObjectId().equals(game.host())) {
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

        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(() -> update());
            }
        }, 0, millisPerUpdate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        refreshTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (currentPlayer != null && game != null && currentPlayer.getObjectId().equals(game.host()))
            game.deleteGame();
        else if (currentPlayer != null && game != null)
            game.removePlayer(currentPlayer.getObjectId());
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
                game.removePlayer(currentPlayer.getObjectId());
                e.printStackTrace();
            }
        }
    }

    public void updatePlayerList() {
        TextView textView = findViewById(R.id.playerList);

        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());

        getPlayers.findInBackground((objects, e) -> {
            StringBuilder newText = new StringBuilder();
            for (ParseObject po : objects) {
                System.out.println("Found player with objectid " + po.getObjectId());
                newText.append(((Player) po).username()).append("\n");
            }
            textView.setText(newText.toString());
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

package com.example.antidote_mobile;

import android.content.DialogInterface;
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
            }
            for (Player p : list) {
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

    public void startGame(View v) {
        if (game.numPlayers() < 2) return;

        game.initialize(currentPlayer);

        try {
            game.save();
        } catch (ParseException e) {
            Utilities.showTwoPromptAlert(this,
                    R.string.could_not_connect,
                    R.string.check_your_internet,
                    R.string.try_again,
                    R.string.cancel,
                    (dialog, which) -> {
                        startGame(v);
                        dialog.dismiss();
                    }, (dialog, which) -> dialog.dismiss());
        }
    }

    public void goToGameScreen() {
        Intent goToGame = new Intent(LobbyActivity.this, GameActivity.class);
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        sendGame.putSerializable("currentPlayer", currentPlayer);
        goToGame.putExtras(sendGame);
        startActivity(goToGame);

        LobbyActivity.this.finish();
    }
}

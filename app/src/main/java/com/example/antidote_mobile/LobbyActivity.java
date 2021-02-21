package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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

        if (!currentPlayer.objectId.equals(game.host)) {
            Button startGameButton = findViewById(R.id.startGameButton);
            Button endGameButton = findViewById(R.id.endGameButton);
        }

        TextView roomCodeTextView = findViewById(R.id.roomCodeTextView);
        roomCodeTextView.setText(game.roomCode);

        updatePlayerList();

        refreshTimer = new Timer();

        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(() -> updatePlayerList());
            }
        }, millisPerUpdate, millisPerUpdate);

    }

    public void updatePlayerList() {
        TextView textView = findViewById(R.id.playerList);

        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players);

        getPlayers.findInBackground((objects, e) -> {
            ArrayList<String> userObjectIds = new ArrayList<>();
            for (ParseObject po : objects) {
                userObjectIds.add(po.getString("who"));
                System.out.println("Found player with objectid " + po.getObjectId());
            }
            System.out.println("User object ids for refresh: " + userObjectIds);

            ParseQuery<ParseUser> getUsers = ParseUser.getQuery();
            getUsers.whereContainedIn("objectId", userObjectIds);
            getUsers.findInBackground((objects1, e1) -> {
                StringBuilder newText = new StringBuilder();
                for (ParseUser user : objects1) {
                    System.out.println("Found user with objectid " + user.getObjectId());
                    newText.append(user.getUsername());
                    newText.append("\n");
                }
                textView.setText(newText.toString());
            });
        });


    }

    public void endGame(View v) {}

    public void startGame(View v) {
        // Mess with <game>'s parameters
        // Save them
        // Mess with players' cards
        // Save them
        // Enter game screen

        Intent goToLobby = new Intent(LobbyActivity.this, GameActivity.class);
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        goToLobby.putExtras(sendGame);
        startActivity(goToLobby);
    }
}

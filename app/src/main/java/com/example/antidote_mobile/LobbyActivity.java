package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {

    Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        game = (Game) getIntent().getSerializableExtra("gameInfo");

        TextView roomCodeTextView = findViewById(R.id.roomCodeTextView);
        roomCodeTextView.setText(game.roomCode);

        updatePlayerList();
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
                for (ParseUser user : objects1) {
                    System.out.println("Found user with objectid " + user.getObjectId());
                    textView.append(user.getUsername() + "\n");
                }
            });
        });
    }

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

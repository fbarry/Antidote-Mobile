package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseObject;
import com.parse.ParseQuery;

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

        for (String playerId : game.players) {
            ParseQuery<ParseObject> getPlayer = new ParseQuery<>("Player");
            getPlayer.include("user");

            getPlayer.getInBackground(playerId, (object, e) -> {
                if (e == null) {
                    textView.append("PLAYER - \n");
                    textView.append(object.getString("username") + "\n");
                } else {
                    textView.append("Unknown... mysterious\n");
                }
            });
        }
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

package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class GameOverActivity extends AppCompatActivity {

    Game game;
    Player currentPlayer;
    ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.activity_gameover);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");
        // players = (ArrayList<Player>) getIntent().getSerializableExtra("players");
    }

    public void goToLobbyActivity(View v) {
        Intent goToLobby = new Intent(GameOverActivity.this, LobbyActivity.class);
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        sendGame.putSerializable("currentPlayer", currentPlayer);
        goToLobby.putExtras(sendGame);
        startActivity(goToLobby);
        GameOverActivity.this.finish();
    }
}

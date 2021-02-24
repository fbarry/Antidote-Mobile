package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    Game game;
    Player currentPlayer;
    CardHandler ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        ch = findViewById(R.id.cardHandler);
        ch.setCards(currentPlayer.cards());

        TextView gameCodeTextView = findViewById(R.id.gameCodeTextView);
        gameCodeTextView.append(" " + game.roomCode());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (game.host().equals(currentPlayer.getObjectId())) {
            game.deleteGame();
        } else {
            game.removePlayer(currentPlayer.getObjectId());
        }
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(GameActivity.this, InfoPageActivity.class));
    }

}

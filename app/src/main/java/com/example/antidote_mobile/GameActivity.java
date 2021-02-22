package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

public class GameActivity extends AppCompatActivity {

    Game game;
    CardHandler ch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        ch = findViewById(R.id.cardHandler);
        ch.setCards(Arrays.asList("SYRINGE", "TOXIN.SERUM-N", "ANTIDOTE.RUBIMAXB.4", "ANTIDOTE.MX-VILE.2", "SYRINGE"));

        TextView gameCodeTextView = findViewById(R.id.gameCodeTextView);
        gameCodeTextView.append(" "+game.roomCode);


    }

    public void openInfoPage(View v) {
        startActivity(new Intent(GameActivity.this, InfoPageActivity.class));
    }

}

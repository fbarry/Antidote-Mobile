package com.example.antidote_mobile;

import android.os.Bundle;

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

    }

}

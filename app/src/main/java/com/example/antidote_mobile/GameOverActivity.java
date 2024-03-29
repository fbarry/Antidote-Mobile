package com.example.antidote_mobile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

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
        //noinspection unchecked
        players = (ArrayList<Player>) getIntent().getSerializableExtra("players");

        hideEverything();
        for (int i = 0; i < players.size(); i++) {
            TextView tv = getTextView(i);
            tv.setVisibility(View.VISIBLE);
            tv.setText(players.get(i).username());
            tv.append(" : [ " + players.get(i).points() + " ] ");
            if (players.get(i).lastRoundPoints() >= 0) tv.append("+");
            tv.append(players.get(i).lastRoundPoints() + "");
        }

        ImageView toxinView = findViewById(R.id.toxinImageView);
        System.out.println(game.toxin().getText() + " is the toxin at the end of the game");
        Drawable img = ResourcesCompat.getDrawable(getResources(), game.toxin().getResX(), null);
        System.out.println(img);
        toxinView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), game.toxin().getResX(), null));

        updateWinStats();

        currentPlayer.setNeedsGameOverScreen(false);
        currentPlayer.saveInBackground();
        game.setNumCards(0);
        game.saveInBackground();
    }

    public void updateWinStats() {
        User user = AntidoteMobile.currentUser;
        Stats stats = user.getStats();

        if (currentPlayer.lastRoundPoints() >= 0) {
            stats.incrementWins();
        } else {
            stats.incrementLosses();
        }

        stats.saveInBackground(e -> {
            if (e == null) System.out.println("SUCCESS");
            else e.printStackTrace();
        });
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

    private TextView getTextView(int playerIndex) {
        switch (playerIndex) {
            case 0:
                return findViewById(R.id.gameOver_player1TextView);
            case 1:
                return findViewById(R.id.gameOver_player2TextView);
            case 2:
                return findViewById(R.id.gameOver_player3TextView);
            case 3:
                return findViewById(R.id.gameOver_player4TextView);
            case 4:
                return findViewById(R.id.gameOver_player5TextView);
            case 5:
                return findViewById(R.id.gameOver_player6TextView);
            case 6:
                return findViewById(R.id.gameOver_player7TextView);
        }
        return findViewById(R.id.gameOver_player7TextView);
    }

    private void hideEverything() {
        for (int i = 0; i < 7; i++) getTextView(i).setVisibility(View.GONE);
    }
}

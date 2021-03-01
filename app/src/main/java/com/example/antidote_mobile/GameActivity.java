package com.example.antidote_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    Game game;
    Player currentPlayer;
    CardHandler ch;
    ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());

        players = new ArrayList<>();
        try {
            List<ParseObject> parseObjects = getPlayers.find();
            for(ParseObject currObject : parseObjects){
                players.add((Player) currObject);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ch = findViewById(R.id.cardHandler);
        ch.setCards(currentPlayer.cards());

        TextView gameCodeTextView = findViewById(R.id.gameCodeTextView);
        gameCodeTextView.append(" " + game.roomCode());

        switch (game.numPlayers()) {
            case 1:
            case 2:
                findViewById(R.id.player3workstation).setVisibility(View.GONE);
                findViewById(R.id.player3TextView).setVisibility(View.GONE);
            case 3:
                findViewById(R.id.player4workstation).setVisibility(View.GONE);
                findViewById(R.id.player4TextView).setVisibility(View.GONE);
            case 4:
                findViewById(R.id.player5workstation).setVisibility(View.GONE);
                findViewById(R.id.player5TextView).setVisibility(View.GONE);
            case 5:
                findViewById(R.id.player6workstation).setVisibility(View.GONE);
                findViewById(R.id.player6TextView).setVisibility(View.GONE);
            case 6:
                findViewById(R.id.player7workstation).setVisibility(View.GONE);
                findViewById(R.id.player7TextView).setVisibility(View.GONE);
        }
        switch (game.numPlayers()) {
            case 7:
                ((TextView) findViewById(R.id.player7TextView)).setText(game.players().get(6));
            case 6:
                ((TextView) findViewById(R.id.player6TextView)).setText(game.players().get(5));
            case 5:
                ((TextView) findViewById(R.id.player5TextView)).setText(game.players().get(4));
            case 4:
                ((TextView) findViewById(R.id.player4TextView)).setText(game.players().get(3));
            case 3:
                ((TextView) findViewById(R.id.player3TextView)).setText(game.players().get(2));
            case 2:
                ((TextView) findViewById(R.id.player2TextView)).setText(game.players().get(1));
            case 1:
                ((TextView) findViewById(R.id.player1TextView)).setText(game.players().get(0));
        }


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

    public void workstation1(View v) {
        viewWorkstation(0);
    }

    public void workstation2(View v) {
        viewWorkstation(1);
    }

    public void workstation3(View v) {
        viewWorkstation(2);
    }

    public void workstation4(View v) {
        viewWorkstation(3);
    }

    public void workstation5(View v) {
        viewWorkstation(4);
    }

    public void workstation6(View v) {
        viewWorkstation(5);
    }

    public void workstation7(View v) {
        viewWorkstation(6);
    }

    // playerNum is zero-indexed
    public void viewWorkstation(int playerNum) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.activity_workstation);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");

        myDialog.show();

        CardHandler workstationCh = myDialog.findViewById(R.id.cardHandlerWorkstation);

        if(workstationCh == null){
            System.err.println("!!! brother.");
            return;
        }

        workstationCh.setCards(players.get(playerNum).cards());
    }

}

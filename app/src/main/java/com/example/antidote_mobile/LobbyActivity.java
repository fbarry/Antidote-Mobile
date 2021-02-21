package com.example.antidote_mobile;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
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
            ParseQuery<ParseObject> getPlayer = new ParseQuery<ParseObject>("Player");
            getPlayer.include("user");

            getPlayer.getInBackground(playerId, new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        textView.append("PLAYER\n");
                    } else {
                        textView.append("Unknown... mysterious\n");
                    }
                }
            });
        }
    }
}

package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilePageActivity extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);

        TextView title = (TextView) findViewById(R.id.profileTitle);

        title.setText(AntidoteMobile.currentUser.getUsername());
        title.append("'s Profile");


        int dummyGamesWon = 3;
        int dummyGamesLost = 4;
        int dummyGamesPlayed = dummyGamesWon + dummyGamesLost;
        double dummyWinRate = 0;
        if (dummyGamesPlayed != 0) {
            dummyWinRate = (dummyGamesWon * 100.0) / (dummyGamesPlayed);
        }

        TextView gamesPlayed = (TextView) findViewById(R.id.gamesPlayed);
        TextView gamesWon = (TextView) findViewById(R.id.gamesWon);
        TextView gamesLost = (TextView) findViewById(R.id.gamesLost);
        TextView winRate = (TextView) findViewById(R.id.winRate);

        gamesPlayed.setText("Games Played: ");
        gamesPlayed.append(dummyGamesPlayed + "");

        gamesWon.setText("Games Won: ");
        gamesWon.append(dummyGamesWon + "");

        gamesLost.setText("Games Lost: ");
        gamesLost.append(dummyGamesLost + "");

        winRate.setText("Win Rate: ");
        winRate.append((Math.round(dummyWinRate * 100.0) / 100.0) + "%");
    }

    public void goBack(View v) {
        ProfilePageActivity.this.finish();
    }


}
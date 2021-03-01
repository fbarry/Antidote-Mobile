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

        User user = AntidoteMobile.currentUser;

        setContentView(R.layout.profile_page);

        TextView title = (TextView) findViewById(R.id.profileTitle);

        title.setText(user.getUsername());
        title.append("'s Profile");

        TextView gamesPlayed = (TextView) findViewById(R.id.gamesPlayed);
        TextView gamesWon = (TextView) findViewById(R.id.gamesWon);
        TextView gamesLost = (TextView) findViewById(R.id.gamesLost);
        TextView winRate = (TextView) findViewById(R.id.winRate);

        gamesPlayed.setText("Games Played: ");
        gamesPlayed.append(user.getTotalGames() + "");

        gamesWon.setText("Games Won: ");
        gamesWon.append(user.getNumberOfWins() + "");

        gamesLost.setText("Games Lost: ");
        gamesLost.append(user.getNumberOfLoses() + "");

        winRate.setText("Win Rate: ");
        winRate.append(user.getWinRate() + "%");
    }

    public void goBack(View v) {
        ProfilePageActivity.this.finish();
    }


}
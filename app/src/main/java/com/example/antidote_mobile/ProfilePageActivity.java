package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ProfilePageActivity extends AppCompatActivity {

    static User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = AntidoteMobile.currentUser;

        setContentView(R.layout.profile_page);


        TextView status = findViewById(R.id.statusMessage);
        EditText changeStatus = findViewById(R.id.changeStatusText);

        status.setPaintFlags(View.INVISIBLE);
        changeStatus.setPaintFlags(View.INVISIBLE);

        if (user.getStatus() == null)
            status.setText("I'm a pro Antidote Mobile gamer!");
        else
            status.setText(user.getStatus());

        TextView title = findViewById(R.id.profileTitle);

        title.setText(user.getUsername());
        title.append("'s Profile");

        TextView gamesPlayed = findViewById(R.id.gamesPlayed);
        TextView gamesWon = findViewById(R.id.gamesWon);
        TextView gamesLost = findViewById(R.id.gamesLost);
        TextView winRate = findViewById(R.id.winRate);

        gamesPlayed.setText("Games Played: ");
        gamesPlayed.append(user.getTotalGames() + "");

        gamesWon.setText("Games Won: ");
        gamesWon.append(user.getNumberOfWins() + "");

        gamesLost.setText("Games Lost: ");
        gamesLost.append(user.getNumberOfLoses() + "");

        winRate.setText("Win Rate: ");
        winRate.append(user.getWinRate() + "%");
    }

    public void editStatus(View v) {
        Button editButton = findViewById(R.id.statusEditButton);
        Button saveButton = findViewById(R.id.statusSaveButton);

        TextView status = findViewById(R.id.statusMessage);
        EditText changeStatus = findViewById(R.id.changeStatusText);

        editButton.setVisibility(View.GONE);
        status.setVisibility(View.INVISIBLE);

        saveButton.setVisibility(View.VISIBLE);
        changeStatus.setVisibility(View.VISIBLE);
    }

    public void saveStatus(View v) {
        Button editButton = findViewById(R.id.statusEditButton);
        Button saveButton = findViewById(R.id.statusSaveButton);

        TextView status = findViewById(R.id.statusMessage);
        EditText changeStatus = findViewById(R.id.changeStatusText);

        editButton.setVisibility(View.VISIBLE);
        status.setVisibility(View.VISIBLE);

        saveButton.setVisibility(View.GONE);
        changeStatus.setVisibility(View.GONE);

        user.setStatus(changeStatus.getText().toString());
        status.setText(user.getStatus());
        changeStatus.setText("");
    }

    public void goBack(View v) {
        ProfilePageActivity.this.finish();
    }


}
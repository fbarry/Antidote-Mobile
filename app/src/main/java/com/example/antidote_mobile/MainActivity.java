package com.example.antidote_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("unused")
    private DrawerLayout drawer;

    Player currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//
//        CardHandler ch = findViewById(R.id.cardHandler);
//        ch.setCards("ANTIDOTE.SERUM-N.5");
        AntidoteMobile.currentUser = User.signIn("randomUser", "randomPassword");

        updateDisplayedUsername();

    }

    void updateDisplayedUsername() {
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(R.string.hey_there);
        usernameTextView.append(AntidoteMobile.currentUser.username);
        usernameTextView.append("!");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void onCreateGame(View v) {
        currentPlayer = new Player().createPlayer(AntidoteMobile.currentUser);
        if (currentPlayer == null) {
            // failed
            System.out.println("FAILED TO CREATE NEW PLAYER");
        } else {
            goToLobbyActivity(Game.createGame(currentPlayer));
        }
    }

    public void onJoinGame(View v) {
        TextView gameCodeTextView = findViewById(R.id.joinCodeTextView);
        String gameCode = gameCodeTextView.getText().toString();

        // If a player exists with us as its pointer, get that guy,
        if (AntidoteMobile.currentUser.objectId != null) {
            // It's at least possible that a player exists with us, since we're
            // registered with the database. Go find a Player with us

            ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
            query.whereEqualTo("who", AntidoteMobile.currentUser.objectId);

            try {
                ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
                System.out.println(candidates.size() + " potential Players found (should be 0 or 1)");
                for (ParseObject obj : candidates) {
                    if (obj.getString("who").equals(AntidoteMobile.currentUser.objectId)) {
                        currentPlayer = new Player(obj);
                        break;
                    }
                }
            } catch (ParseException e) {
                System.out.println("Coulnd't find an existing Player with current user, making one");
                currentPlayer = null;
            }

            if (currentPlayer != null) {
                // We found a player, so rejoin the game it's in
                goToLobbyActivity(Game.rejoinGame(currentPlayer));
                return;
            }
        }

        // We couldn't find an existing Player to use, so let's try to make one

        if (gameCode.length() == 0) return;

        System.out.println("TRY TO JOIN: " + gameCode);
        currentPlayer = new Player().createPlayer(AntidoteMobile.currentUser);


        if (currentPlayer == null) {
            System.out.println("FAILED TO CREATE NEW PLAYER");
        } else {
            goToLobbyActivity(Game.joinGame(gameCode, currentPlayer));
        }
    }

    public void goToLobbyActivity(Game game) {
        if (game == null) {
            // join failed, show alert
            System.out.println("FAILED TO CREATE/JOIN GAME");
        } else {
            Intent goToLobby = new Intent(MainActivity.this, LobbyActivity.class);
            Bundle sendGame = new Bundle();
            sendGame.putSerializable("gameInfo", game);
            goToLobby.putExtras(sendGame);
            startActivity(goToLobby);
        }
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }

    public void launchLoginWindowDialog(View v) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.login_popup);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");
        Button login = myDialog.findViewById(R.id.login_loginButton);

        myDialog.show();

        //noinspection Convert2Lambda
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = myDialog.findViewById(R.id.login_usernameEntry);
                EditText password = myDialog.findViewById(R.id.login_passwordEntry);
                TextView message = myDialog.findViewById(R.id.login_textViewMessage);

                AntidoteMobile.currentUser = User.signIn(username.getText().toString(), password.getText().toString());

                password.getText().clear();
                if (AntidoteMobile.currentUser != null) {
                    username.getText().clear();
                    updateDisplayedUsername();
                    myDialog.dismiss();
                } else {
                    message.setText(R.string.login_failed);
                }

            }
        });
    }

}
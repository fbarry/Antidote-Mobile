package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Objects;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @SuppressWarnings("unused")
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    Player currentPlayer;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // toolbar
        setSupportActionBar(toolbar);

        // navigation drawer menu
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

//        AntidoteMobile.currentUser = User.signIn("randomUser", "randomPassword");

        updateDisplayedUsername();

        if (AntidoteMobile.currentUser != null && !AntidoteMobile.currentUser.isGuest()) {
            Button loginbutton = findViewById(R.id.loginButton);
            loginbutton.setVisibility(GONE);
        }

    }

    @Override
    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    void updateDisplayedUsername() {
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(R.string.hey_there);
        usernameTextView.append(" " + AntidoteMobile.currentUser.getUsername());
        usernameTextView.append("!");
    }
    public void onCreateGame(View v) {
        currentPlayer = new Player().createPlayer(AntidoteMobile.currentUser);
        if (currentPlayer == null) {
            System.out.println("FAILED TO CREATE NEW PLAYER");
        } else {
            goToLobbyActivity(Game.createGame(currentPlayer));
        }
    }

    public void onJoinGame(View v) {
        TextView gameCodeTextView = findViewById(R.id.joinCodeTextView);
        String gameCode = gameCodeTextView.getText().toString();
        currentPlayer = null;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
        query.whereEqualTo("who", AntidoteMobile.currentUser.getObjectId());

        try {
            ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
            System.out.println(candidates.size() + " potential Players found (should be 0 or 1)");
            for (ParseObject obj : candidates) {
                if (Objects.equals(obj.getString("who"), AntidoteMobile.currentUser.getObjectId())) {
                    currentPlayer = new Player(obj);
                    break;
                }
            }
        } catch (ParseException e) {
            System.out.println("Couldn't find an existing Player with current user, making one");
        }

        if (currentPlayer != null) {
            goToLobbyActivity(Game.rejoinGame(currentPlayer));
            return;
        }

        if (gameCode.length() == 0) return;

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
            sendGame.putSerializable("currentPlayer", currentPlayer);
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
            @SuppressLint("SetTextI18n")
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

                    Button loginbutton = findViewById(R.id.loginButton);
                    loginbutton.setText("Profile");
                } else {
                    message.setText(R.string.login_failed);
                }

            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return true;
    }
}
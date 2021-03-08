package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        updateDisplayedUsername();

        if (AntidoteMobile.currentUser != null && !AntidoteMobile.currentUser.isGuest()) {
            Button loginbutton = findViewById(R.id.loginButton);
            loginbutton.setVisibility(GONE);
        }

        navigationView.setCheckedItem(R.id.nav_dashboard);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        if (!sp.getBoolean("logged", false)) {
            MainActivity.this.finish();
        }
    }

    public void gotoMenu(Class dest) {
        startActivity(new Intent(MainActivity.this, dest));
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                break;
            case R.id.nav_profile:
                gotoMenu(ProfilePageActivity.class);
                break;
            case R.id.nav_stats:
                gotoMenu(StatsActivity.class);
                break;
            case R.id.nav_logout:
                SharedPreferences sp;
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
                sp.edit().putString("currentUser", "ERROR: NOT SET").apply();
                ParseUser.logOutInBackground();

                MainActivity.this.finish();

                gotoMenu(LoginActivity.class);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateDisplayedUsername() {
        TextView usernameTextView = findViewById(R.id.usernameTextView);
        usernameTextView.setText(R.string.hey_there);
        usernameTextView.append(" " + AntidoteMobile.currentUser.getUsername());
        usernameTextView.append("!");
    }


    public void onCreateGame(View v) {
        currentPlayer = Player.createPlayer(AntidoteMobile.currentUser, true);
        if (currentPlayer == null) {
            Utilities.showInformationAlert(this,
                    R.string.create_player_error,
                    R.string.check_other_games_and_internet,
                    null);
        } else {
            goToLobbyActivity(Game.createGame(currentPlayer));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onJoinGame(View v) {
        TextView gameCodeTextView = findViewById(R.id.joinCodeTextView);
        String gameCode = gameCodeTextView.getText().toString();

        currentPlayer = null;

        checkIfCurrentPlayerAlreadyExists();

        if (currentPlayer != null) {
            Game game = Game.rejoinGame(currentPlayer);
            if (gameCode.length() != 0) {
                Utilities.showTwoPromptAlert(this,
                        R.string.confirm_leave_rejoin,
                        R.string.lose_progress_warning,
                        R.string.rejoin,
                        R.string.leave,
                        (dialog, which) -> goToLobbyActivity(game),
                        (dialog, which) -> {
                            if (game != null) game.removePlayer(currentPlayer.getObjectId());
                            else currentPlayer.deleteInBackground();
                            playerJoiningNewGame(gameCode);
                        });
            } else {
                goToLobbyActivity(game);
            }
        } else if (gameCode.length() != 0) {
            playerJoiningNewGame(gameCode);
        }
    }

    public void checkIfCurrentPlayerAlreadyExists() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
        query.whereEqualTo("who", AntidoteMobile.currentUser.getObjectId());

        try {
            ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
            for (ParseObject obj : candidates) {
                if (Objects.equals(obj.getString("who"), AntidoteMobile.currentUser.getObjectId())) {
                    currentPlayer = (Player) obj;
                    break;
                }
            }
        } catch (ParseException e) {
            System.out.println("Couldn't find an existing Player with current user, making one");
        }
    }

    public void playerJoiningNewGame(String gameCode) {
        currentPlayer = Player.createPlayer(AntidoteMobile.currentUser, false);

        if (currentPlayer == null) {
            Utilities.showInformationAlert(this,
                    R.string.create_player_error,
                    R.string.check_your_internet,
                    null);
        } else {
            Game game = Game.joinGame(gameCode, currentPlayer);
            if (game == null) {
                currentPlayer.deleteInBackground();
                currentPlayer = null;
                Utilities.showInformationAlert(this,
                        R.string.enter_lobby_error,
                        R.string.check_game_code_and_internet,
                        null);
            } else {
                if (game.numCards() > 0) {
                    currentPlayer.deleteInBackground();
                    currentPlayer = null;
                    Utilities.showInformationAlert(this,
                            R.string.enter_lobby_error,
                            R.string.game_in_progress,
                            null);
                } else {
                    goToLobbyActivity(game);
                }
            }
        }
    }

    public void goToLobbyActivity(Game game) {
        if (game == null) {
            currentPlayer.deleteInBackground();
            currentPlayer = null;
            Utilities.showInformationAlert(this,
                    R.string.enter_lobby_error,
                    R.string.check_your_internet,
                    null);
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
        Button signup = myDialog.findViewById(R.id.loginPopupSignUpButton);

        myDialog.show();

        signup.setOnClickListener(v1 ->
                startActivity(new Intent(MainActivity.this, SignUpActivity.class)));

        login.setOnClickListener(v1 -> {

            EditText username = myDialog.findViewById(R.id.login_usernameEntry);
            EditText password = myDialog.findViewById(R.id.login_passwordEntry);
            TextView message = myDialog.findViewById(R.id.login_textViewMessage);

            AntidoteMobile.currentUser = User.signIn(username.getText().toString(), password.getText().toString());

            password.getText().clear();
            if (AntidoteMobile.currentUser != null) {
                username.getText().clear();
                updateDisplayedUsername();
                Button loginbutton = findViewById(R.id.loginButton);
                loginbutton.setVisibility(GONE);
                myDialog.dismiss();
            } else {
                message.setText(R.string.login_failed);
            }

        });
    }
}
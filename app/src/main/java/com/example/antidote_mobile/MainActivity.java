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

        navigationView.setCheckedItem(R.id.nav_home);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                break;
            case R.id.nav_tab1:
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_tab2:
                Intent intent2 = new Intent(MainActivity.this, ThirdActivity.class);
                startActivity(intent2);
                break;
            case R.id.nav_share:
                Toast.makeText(this, "Share", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_profile:
                startActivity(new Intent(MainActivity.this, ProfilePageActivity.class));
                break;
            case R.id.nav_logout:
                SharedPreferences sp;
                sp = getSharedPreferences("login", MODE_PRIVATE);
                sp.edit().putBoolean("logged", false).apply();
                sp.edit().putString("currentUser", "ERROR: NOT SET").apply();
                ParseUser.logOutInBackground();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                MainActivity.this.finish();
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

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Player");
        query.whereEqualTo("who", AntidoteMobile.currentUser.getObjectId());

        try {
            ArrayList<ParseObject> candidates = (ArrayList<ParseObject>) query.find();
            System.out.println(candidates.size() + " potential Players found (should be 0 or 1)");
            for (ParseObject obj : candidates) {
                if (Objects.equals(obj.getString("who"), AntidoteMobile.currentUser.getObjectId())) {
                    currentPlayer = (Player) obj;
                    break;
                }
            }
        } catch (ParseException e) {
            System.out.println("Couldn't find an existing Player with current user, making one");
        }

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
                            notRejoin(gameCode);
                        });
            } else {
                goToLobbyActivity(game);
            }
        } else if (gameCode.length() != 0) {
            notRejoin(gameCode);
        }
    }

    public void notRejoin(String gameCode) {
        currentPlayer = Player.createPlayer(AntidoteMobile.currentUser, false);

        if (currentPlayer == null) {
            Utilities.showInformationAlert(this,
                    R.string.create_player_error,
                    R.string.check_your_internet,
                    null);
        } else {
            Game game = Game.joinGame(gameCode, currentPlayer);
            if (game == null) {
                Utilities.showInformationAlert(this,
                        R.string.enter_lobby_error,
                        R.string.check_game_code_and_internet,
                        null);
            } else {
                goToLobbyActivity(game);
            }
        }
    }

    public void goToLobbyActivity(Game game) {
        if (game == null) {
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
}
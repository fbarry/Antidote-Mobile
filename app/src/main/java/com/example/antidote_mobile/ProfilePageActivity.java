package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.parse.ParseUser;

public class ProfilePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;

    static User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        user = AntidoteMobile.currentUser;


        TextView status = findViewById(R.id.statusMessage);
        EditText changeStatus = findViewById(R.id.changeStatusText);

        Button editButton = findViewById(R.id.statusEditButton);

        if (user.isGuest()) {
            editButton.setVisibility(View.GONE);
        } else {
            editButton.setVisibility(View.VISIBLE);
        }

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

    @SuppressLint("NonConstantResourceId")
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_dashboard:
                ProfilePageActivity.this.finish();
                break;
            case R.id.nav_profile:
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

                gotoMenu(LoginActivity.class);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoMenu(Class dest) {
        ProfilePageActivity.this.finish();
        startActivity(new Intent(ProfilePageActivity.this, dest));
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ProfilePageActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RedirectToProfile {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    RecyclerView friendsList;
    ArrayList<String> friends;
    FriendAdapter adapter;
    StatsDialog statsDialog;

    User user;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) user = AntidoteMobile.currentUser;

        statsDialog = new StatsDialog(this, user);

        friends = user.getFriends();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);
        friendsList = findViewById(R.id.friendsList);

        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        friendsList.setLayoutManager(llm);

        adapter = new FriendAdapter(this, friends);
        friendsList.setAdapter(adapter);

        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        TextView status = findViewById(R.id.statusMessage);
        EditText changeStatus = findViewById(R.id.changeStatusText);

        TextView friendsTitle = findViewById(R.id.friendsTitle);
        Button editButton = findViewById(R.id.statusEditButton);
        Button friendButton = findViewById(R.id.friendButton);

        if (!user.getObjectId().equals(AntidoteMobile.currentUser.getObjectId())) {
            editButton.setVisibility(View.GONE);
            if (AntidoteMobile.currentUser.getFriends().contains(user.getObjectId())) {
                friendButton.setText(R.string.unfriend);
            }
        } else {
            friendButton.setVisibility(View.GONE);
        }

        status.setPaintFlags(View.INVISIBLE);
        changeStatus.setPaintFlags(View.INVISIBLE);

        if (user.isGuest()) {
            editButton.setVisibility(View.GONE);
            friendButton.setVisibility(View.GONE);
            friendsList.setVisibility(View.GONE);
            friendsTitle.setVisibility(View.GONE);
            status.setText("Create an account to have a status and friends list!");
        } else {
            if (user.getStatus() == null) {
                status.setText("I'm a pro Antidote Mobile gamer!");
            } else {
                status.setText(user.getStatus());
            }
        }

        TextView title = findViewById(R.id.profileTitle);

        title.setText(user.getUsername());
        title.append("'s Profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refresh(user.getFriends());
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
                gotoMenu(MainActivity.class);
                break;
            case R.id.nav_profile:
                break;
            case R.id.nav_search:
                gotoMenu(SearchActivity.class);
                break;
            case R.id.nav_logout:
                User.logoutCurrentUser(this);
                gotoMenu(LoginActivity.class);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gotoMenu(Class<?> dest) {
        ProfilePageActivity.this.finish();
        startActivity(new Intent(ProfilePageActivity.this, dest));
    }

    public void onClickFriendButton(View v) {
        Button friendButton = findViewById(R.id.friendButton);
        if (AntidoteMobile.currentUser.getFriends().contains(user.getObjectId())) {
            AntidoteMobile.currentUser.removeFriend(user.getObjectId());
            friendButton.setText(R.string.friend);
        } else {
            AntidoteMobile.currentUser.addFriend(user.getObjectId());
            friendButton.setText(R.string.unfriend);
        }
    }

    public void onClickStats(View v) {
        statsDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void goToProfile(User user) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        Intent intent = new Intent(ProfilePageActivity.this, ProfilePageActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
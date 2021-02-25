package com.example.antidote_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        // Logs current user out for testing purposes
        // sp.edit().putBoolean("logged", false).apply();

        if(sp.getBoolean("logged",false)) {
            String userId = sp.getString("currentUser", "ERROR: NOT SET");

            System.out.println("PERSISTENT");

            try {
                User user = (User) ParseUser.getQuery().get(userId);
                goToDashboard(user);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            setContentView(R.layout.activity_login);
        }
    }

    public void onLogin(View v) {
        String username = ((TextView) findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.editTextPassword)).getText().toString();

        login(username, password);
    }

    public boolean login(String username, String password) {
        if (username.equals("")) return false;
        if (password.equals("")) return false;

        try {
            User user = (User) ParseUser.logIn(username, password);
            goToDashboard(user);
            return true;
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public void onSignUpHere(View v) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void onContinueAsGuest(View v) {
        User user = User.signUpGuest();

        if (user == null) return;

        goToDashboard(user);
    }

    public void goToDashboard(User user) {
        AntidoteMobile.currentUser = user;
        sp.edit().putBoolean("logged", true).apply();
        sp.edit().putString("currentUser", user.getObjectId()).apply();
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}

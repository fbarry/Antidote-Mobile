package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLogin(View v) {
        String username = ((TextView) findViewById(R.id.editTextUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.editTextPassword)).getText().toString();

        if (username.equals("")) return;
        if (password.equals("")) return;

        try {
            AntidoteMobile.currentUser = (User) ParseUser.logIn(username, password);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }

    public void onSignUpHere(View v) {
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }

    public void onContinueAsGuest(View v) {
        AntidoteMobile.currentUser = User.signUpGuest();
        if (AntidoteMobile.currentUser == null) return;

        startActivity(new Intent(LoginActivity.this, MainActivity.class));
    }
}

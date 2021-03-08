package com.example.antidote_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.ParseException;
import com.parse.ParseUser;

public class SignUpActivity extends AppCompatActivity {

    User currentUser;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sp = getSharedPreferences("login", MODE_PRIVATE);

        if (sp.getBoolean("logged", false)) {
            String userId = sp.getString("currentUser", "ERROR: NOT SET");

            try {
                currentUser = (User) ParseUser.getQuery().get(userId);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public void onSignUp(View v) {
        String email = ((TextView) findViewById(R.id.editSignUpTextEmail)).getText().toString();
        String username = ((TextView) findViewById(R.id.editSignUpTextUsername)).getText().toString();
        String password = ((TextView) findViewById(R.id.editSignUpTextPassword)).getText().toString();
        String confirmPassword = ((TextView) findViewById(R.id.editSignUpConfirmTextPassword)).getText().toString();

        if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            return;
        }

        if (!password.equals(confirmPassword)) {
            return;
        }

        if (currentUser == null) currentUser = User.signUp(username, password, email);
        else {
            currentUser.setUsername(username);
            currentUser.setPassword(password);
            currentUser.setEmail(email);
            currentUser.setIsGuest(false);
            try {
                currentUser.save();
            } catch (ParseException e) {
                currentUser = null;
            }
        }

        if (currentUser == null) {
            return;
        }

        goToDashboard();
    }

    public void onLogInHere(View v) {
        SignUpActivity.this.finish();
    }

    public void goToDashboard() {
        AntidoteMobile.currentUser = currentUser;
        sp.edit().putBoolean("logged", true).apply();
        sp.edit().putString("currentUser", currentUser.getObjectId()).apply();
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        SignUpActivity.this.finish();
    }
}

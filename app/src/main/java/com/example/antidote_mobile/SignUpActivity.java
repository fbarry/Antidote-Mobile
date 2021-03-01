package com.example.antidote_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        sp = getSharedPreferences("login", MODE_PRIVATE);
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

        User user = User.signUp(username, password, email);

        if (user == null) {
            return;
        }

        goToDashboard(user);
    }

    public void onLogInHere(View v) {
        SignUpActivity.this.finish();
    }

    public void goToDashboard(User user) {
        AntidoteMobile.currentUser = user;
        sp.edit().putBoolean("logged", true).apply();
        sp.edit().putString("currentUser", user.getObjectId()).apply();
        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
        SignUpActivity.this.finish();
    }
}

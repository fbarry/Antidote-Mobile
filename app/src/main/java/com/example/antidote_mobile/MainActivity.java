package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void viewProfiles(View v) {
        TextView textView = (TextView) findViewById(R.id.textView);

    }

    public void addProfile(View v) {
        TextView textView = (TextView) findViewById(R.id.textView);
        User newUser = new User("randomUser", "randomPassword");
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }

    @SuppressLint("SetTextI18n")
    public void testytest(View v) {
        // Put some code here if you want to test something from the home screen.
        ((TextView) findViewById(R.id.textView)).setText("Hello, gamers!");
    }


}
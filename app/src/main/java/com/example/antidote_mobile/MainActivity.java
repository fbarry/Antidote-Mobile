package com.example.antidote_mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.List;
import java.io.IOException;

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
        Profile newProfile = new Profile("randomUser", "randomPassword");
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }


}
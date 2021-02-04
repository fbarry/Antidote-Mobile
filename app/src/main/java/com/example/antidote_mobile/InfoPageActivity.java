package com.example.antidote_mobile;

import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

public class InfoPageActivity extends AppCompatActivity {


    ScrollView scrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_page);
        scrollView = findViewById(R.id.infoScroll);
    }

    public void goBack(View v) {
        InfoPageActivity.this.finish();
    }

    public void scrollToSetup(View v){
        View setupHeader = findViewById(R.id.header_setup);
        scrollView.smoothScrollTo(0, setupHeader.getTop());
    }

    public void scrollToObjective(View v){
        View setupHeader = findViewById(R.id.header_objective);
        scrollView.smoothScrollTo(0, setupHeader.getTop());
    }

    public void scrollToGameplay(View v){
        View setupHeader = findViewById(R.id.header_gameplay);
        scrollView.smoothScrollTo(0, setupHeader.getTop());
    }



}
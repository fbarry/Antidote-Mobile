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

    public void scrollToSetup(View v) {
        View setupHeader = findViewById(R.id.header_setup);
        scrollView.smoothScrollTo(0, setupHeader.getTop());
    }

    public void scrollToObjective(View v) {
        View objectiveHeader = findViewById(R.id.header_objective);
        scrollView.smoothScrollTo(0, objectiveHeader.getTop());
    }

    public void scrollToGameplay(View v) {
        View gameplayHeader = findViewById(R.id.header_gameplay);
        scrollView.smoothScrollTo(0, gameplayHeader.getTop());
    }

    public void scrollToDiscard(View v) {
        View discardSubheader = findViewById(R.id.subheader_discard);
        scrollView.smoothScrollTo(0, discardSubheader.getTop());
    }

    public void scrollToTrade(View v) {
        View tradeSubheader = findViewById(R.id.subheader_trade);
        scrollView.smoothScrollTo(0, tradeSubheader.getTop());
    }

    public void scrollToSyringe(View v) {
        View syringeSubheader = findViewById(R.id.subheader_syringe);
        scrollView.smoothScrollTo(0, syringeSubheader.getTop());
    }

    public void scrollToScoring(View v) {
        View scoringHeader = findViewById(R.id.header_scoring);
        scrollView.smoothScrollTo(0, scoringHeader.getTop());
    }


}
package com.example.antidote_mobile;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

public class StatsDialog extends Dialog {

    Activity activity;
    User user;

    public StatsDialog(Activity activity, User user) {
        super(activity);
        this.activity = activity;
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_stats);

        TextView statsTitle = findViewById(R.id.statsTitle);
        TextView statsAll = findViewById(R.id.statsAll);

        statsTitle.setText(user.getUsername());
        statsTitle.append("'s Statistics");

        statsAll.setText(user.getStats());
    }
}

package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    Player currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CardHandler ch = findViewById(R.id.cardHandler);
        ch.setCards("ANTIDOTE.SERUM-N.5");
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }

    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    public void testytest(View v) {
        // Put some code here if you want to test something from the home screen.
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        if (AntidoteMobile.currentUser != null) {
            textView.setText(AntidoteMobile.currentUser.username + "," + AntidoteMobile.currentUser.email);

        }
    }

    @SuppressWarnings("unused")
    @SuppressLint("SetTextI18n")
    public void testytest2(View v) {
        // Put some code here if you want to test something from the home screen.
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        currentPlayer = new Player(AntidoteMobile.currentUser);
        textView2.setText(currentPlayer.objectId+", "+currentPlayer.user.toString());
    }

    public void launchLoginWindowDialog(View v) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.login_popup);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");
        Button login = myDialog.findViewById(R.id.login_loginButton);

        myDialog.show();

        //noinspection Convert2Lambda
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText username = myDialog.findViewById(R.id.login_usernameEntry);
                EditText password = myDialog.findViewById(R.id.login_passwordEntry);
                TextView message = myDialog.findViewById(R.id.login_textViewMessage);

                AntidoteMobile.currentUser = User.signIn(username.getText().toString(), password.getText().toString());

                password.getText().clear();
                if (AntidoteMobile.currentUser != null) {
                    username.getText().clear();
                    myDialog.dismiss();
                } else {
                    message.setText(R.string.login_failed);
                }

            }
        });
    }

}
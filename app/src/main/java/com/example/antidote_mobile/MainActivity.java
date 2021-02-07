package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    ParseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }

    @SuppressLint("SetTextI18n")
    public void testytest(View v) throws ParseException {
        // Put some code here if you want to test something from the home screen.
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);
//        User newUser = new User("randomUser3", "randomPassword", new SignUpCallback() {
////            @Override
////            public void done(ParseException e) {
////                if (e == null) {
////                    // Success
////                    textView.setText("SUCCESS");
////                } else {
////                    // Failure
////                    textView.setText(e.getMessage());
////                    e.printStackTrace();
////                }
////            }
////        });
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
        List<ParseObject> thelist = query.find();

        StringBuilder disp = new StringBuilder();
        for (ParseObject object : thelist) {
            disp.append(object.getObjectId()).append(" ");
        }
        textView2.setText(disp.toString());


        //noinspection Convert2Lambda
        query.getInBackground("PK7N8AL2H3", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    String display = "SUCC ";
                    int cardnum = object.getInt("cardNum");
                    String type = object.getString("type");
                    display += cardnum + " ";
                    display += type;
                    textView.setText(display);
                } else {
                    textView.setText(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void logUserIn(View v) {
        EditText username = findViewById(R.id.usernameInput);
        EditText password = findViewById(R.id.passwordInput);

        TextView textView = findViewById(R.id.textView);

        try {
            currentUser = ParseUser.logIn(username.getText().toString(), password.getText().toString());
            textView.setText(currentUser.getUsername() + " you have successfully logged in!");

        } catch (ParseException e) {
            currentUser = null;

            textView.setText("Login failed.");
        } finally {
            password.getText().clear();
        }


    }


}
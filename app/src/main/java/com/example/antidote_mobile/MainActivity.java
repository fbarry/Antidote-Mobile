package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AntidoteMobile.currentUser = User.signIn("randomUser", "randomPassword");

        CardHandler ch = findViewById(R.id.cardHandler);
        ch.setCards("ANTIDOTE.SERUM-N.5");
        System.out.println("LMAOOOO BROOOO");

    }

    public void openInfoPage(View v) {
        startActivity(new Intent(MainActivity.this, InfoPageActivity.class));
    }

    @SuppressLint("SetTextI18n")
    public void testytest(View v) throws ParseException {
        // Put some code here if you want to test something from the home screen.
        TextView textView = findViewById(R.id.textView);
        TextView textView2 = findViewById(R.id.textView2);

        if (AntidoteMobile.currentUser != null) {
            textView.setText(AntidoteMobile.currentUser.username + "," + AntidoteMobile.currentUser.email);

//        if (currentUser != null) {
//            textView.setText(currentUser.getInt("magicnumber") + " is the magic number of " + currentUser.getUsername());
//        } else {
//            textView.setText("you silly billy, you're not logged in");
//            return;
//
//        }

//        currentUser.put("magicnumber", (int) (Math.random() * 100));
//        currentUser.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
////                textView2.setText("Update done! ");
//            }
//        });
//        Object thing = currentUser.get("specialArray");

//        if (thing == null) {
//            textView2.setText("oh noes");
//        } else {
//            textView2.setText(thing.toString() + "," + thing.getClass());

//            textView2.append(" "+po.getString("type"));
//            textView2.append(((ParseObject)thing).get("type").toString());
//            ArrayList<Integer> trythis = (ArrayList<Integer>) thing;
//            textView2.setText(textView2.getText()+"/"+trythis.get(0));
        }

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.include("theCard");
//        query.whereEqualTo("username", currentUser.getUsername());
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> users, ParseException e) {
//                if (e == null) {
//                    // The query was successful, returns the users that matches
//                    // the criterias.
//                    for(ParseUser user : users) {
//                        textView2.append(" "+user.getUsername());
//                        ParseObject card = (ParseObject) user.get("theCard");
//                        textView2.append(" "+card.getString("type"));
//
//                        System.out.println(user.getUsername());
//
//                    }
//                } else {
//                    // Something went wrong.
//                }
//            }
//        });


//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
//        query.getInBackground("ImFD78s4so", new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//
//
//                    textView2.append(" "+object.toString());
//
//                    String display = "SUCC ";
//                    int cardnum = object.getInt("cardNum");
//                    String type = object.getString("type");
//                    display += cardnum + " ";
//                    display += type;
////                    textView2.setText(display);
//                } else {
//                    textView2.setText(e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });


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
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Card");
//        List<ParseObject> thelist = query.find();
//
//        StringBuilder disp = new StringBuilder();
//        for (ParseObject object : thelist) {
//            disp.append(object.getObjectId()).append(" ");
//        }
//        textView2.setText(disp.toString());
//
//
//        //noinspection Convert2Lambda
//        query.getInBackground("PK7N8AL2H3", new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    String display = "SUCC ";
//                    int cardnum = object.getInt("cardNum");
//                    String type = object.getString("type");
//                    display += cardnum + " ";
//                    display += type;
//                    textView.setText(display);
//                } else {
//                    textView.setText(e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        });

    }

    public void launchLoginWindowDialog(View v) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.login_popup);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");
        Button login = (Button) myDialog.findViewById(R.id.login_loginButton);

        myDialog.show();

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
                    message.setText("Login failed.");
                }

            }
        });
    }

}
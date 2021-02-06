package com.example.antidote_mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SignUpCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

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
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textView2 = (TextView) findViewById(R.id.textView2);
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

        String disp = "";
        for(ParseObject object:thelist){
            disp+=object.getObjectId()+" ";
        }
        textView2.setText(disp);




        query.getInBackground("PK7N8AL2H3", new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null){
                    String display = "SUCC ";
                    int cardnum = object.getInt("cardNum");
                    String type = object.getString("type");
                    display+=cardnum+" ";
                    display+=type;
                    textView.setText(display);
                }else{
                    textView.setText(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }


}
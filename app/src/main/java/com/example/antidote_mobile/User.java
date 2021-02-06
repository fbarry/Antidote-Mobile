package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class User {
    private String username;
    private String password;
    private boolean isGuest;

    public User(String username, String password, SignUpCallback performWhenDone) {
        this.username = username;
        this.password = password;

        ParseUser newProfile = new ParseUser();
        newProfile.put("username", username);
        newProfile.put("password", password);

        newProfile.signUpInBackground(performWhenDone);
    }


}

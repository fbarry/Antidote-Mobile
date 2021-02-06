package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

public class User {
    private String username;
    private String password;
    private boolean isGuest;

    public User(String username, String password) {
        this.username = username;
        this.password = password;

        ParseObject newProfile = new ParseObject("Profile");
        newProfile.put("username", username);
        newProfile.put("password", password);

        newProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    e.printStackTrace();
                }
            }
        });
    }


}

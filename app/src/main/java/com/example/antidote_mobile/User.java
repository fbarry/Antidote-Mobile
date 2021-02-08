package com.example.antidote_mobile;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

public class User {
    private String username;
    private String email;

    public User(String username) {
        this.username = username;
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    public static User signIn(String username, String password) {
        try {
            ParseUser returned = ParseUser.logIn(username, password);
            return new User(returned.getUsername(), returned.getEmail());
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signUp(String username, String password, String email) {
        ParseUser newProfile = new ParseUser();
        newProfile.put("username", username);
        newProfile.put("password", password);

        if (email != null)
            newProfile.put("email", email);

        try {
            newProfile.signUp();
            // success! don't need to do much, since we have the stuff ready anyway...
            User ret = new User(username, email);
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signUp(String username, String password) {
        return signUp(username, password, null);
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}

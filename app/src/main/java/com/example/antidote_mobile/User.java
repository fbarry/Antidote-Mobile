package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@SuppressWarnings("unused")
public class User {
    String username, email, objectId;
    boolean isGuest;

    public User() {

    }

    public User(ParseUser po) {
        username = po.getUsername();
        email = po.getEmail();
        objectId = po.getObjectId();
        isGuest = po.getBoolean("isGuest");
    }

    public static User getUser(String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        try {
            ParseObject po = query.get(objectId);
            return new User((ParseUser) po);
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signIn(String username, String password) {
        try {
            ParseUser returned = ParseUser.logIn(username, password);
            return new User(returned);
        } catch (ParseException e) {
            return null;
        }
    }

    public static User getNewGuest() {
        User ret = new User();
        ret.username = "guest_" + (int) (Math.random() * 100000000);
        ret.isGuest = true;
        return ret;
    }

    public static User signUp(String username, String password, String email) {
        ParseUser newProfile = new ParseUser();
        newProfile.put("username", username);
        newProfile.put("password", password);
        newProfile.put("isGuest", false);

        if (email != null)
            newProfile.put("email", email);

        try {
            newProfile.signUp();
            // success! don't need to do much, since we have the stuff ready anyway...
            return new User(newProfile);
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signUpGuest(String username, String password) {
        ParseUser newProfile = new ParseUser();
        newProfile.put("username", username);
        newProfile.put("password", password);
        newProfile.put("isGuest", true);

        try {
            newProfile.signUp();
            // success! don't need to do much, since we have the stuff ready anyway...
            return new User(newProfile);
        } catch (ParseException e) {
            System.out.println("Failed to sign guest up!");
//            e.printStackTrace();
            return null;
        }
    }

    public static User signUp(String username, String password) {
        return signUp(username, password, null);
    }

    @SuppressWarnings("NullableProblems")
    public String toString() {
        return "User: [" + username + "," + objectId + "," + isGuest + "," + email + "]";
    }

}

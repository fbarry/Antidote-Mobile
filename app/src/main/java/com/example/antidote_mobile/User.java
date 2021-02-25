package com.example.antidote_mobile;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@SuppressWarnings("unused")
public class User extends ParseUser {

    public User() {
        super();
    }

    public boolean isGuest() {
        return this.getBoolean("isGuest");
    }

    public void setIsGuest(boolean isGuest) {
        this.put("isGuest", isGuest);
    }

    public static User getUser(String objectId) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        try {
            ParseUser po = query.get(objectId);
            return (User) po;
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signIn(String username, String password) {
        try {
            ParseUser returned = ParseUser.logIn(username, password);
            return (User) returned;
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signUp(String username, String password, String email) {
        User newProfile = new User();
        newProfile.setUsername(username);
        newProfile.setPassword(password);
        newProfile.setIsGuest(false);

        if (email != null) newProfile.setEmail(email);

        try {
            newProfile.signUp();
            // success! don't need to do much, since we have the stuff ready anyway...
            return (User) newProfile;
        } catch (ParseException e) {
            return null;
        }
    }

    public static User signUpGuest() {
        User newProfile = new User();
        newProfile.setUsername("guest_" + (int) (Math.random() * 100000000));
        newProfile.setPassword(AntidoteMobile.guestPassword);
        newProfile.setIsGuest(true);

        try {
            newProfile.signUp();
            return (User) newProfile;
        } catch (ParseException e) {
            System.out.println("Failed to sign guest up!");
            return null;
        }
    }

    public static User signUp(String username, String password) {
        return signUp(username, password, null);
    }

}

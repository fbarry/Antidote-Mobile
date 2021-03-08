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

    public int getNumberOfWins() {
        return this.getInt("numberOfWins");
    }

    public void setNumberOfWins(int numberOfWins) {
        this.put("numberOfWins", numberOfWins);
    }

    public int getNumberOfLoses() {
        return this.getInt("numberOfLoses");
    }

    public void setNumberOfLoses(int numberOfLoses) {
        this.put("numberOfLoses", numberOfLoses);
    }

    public int getTotalGames() {
        return getNumberOfWins() + getNumberOfLoses();
    }

    public String getStatus() {
        return this.getString("statusMessage");
    }

    public void setStatus(String status) {
        this.put("statusMessage", status);
    }

    public double getWinRate() {
        if (getTotalGames() == 0)
            return 0;
        return (Math.round(((this.getNumberOfWins() * 100.0) / this.getTotalGames()) * 100.0) / 100.0);
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
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static User signUp(String username, String password, String email) {
        User newProfile = new User();
        newProfile.setUsername(username);
        newProfile.setPassword(password);
        newProfile.setIsGuest(false);

        newProfile.setNumberOfWins(0);
        newProfile.setNumberOfLoses(0);

        if (email != null) newProfile.setEmail(email);

        try {
            newProfile.signUp();
            // success! don't need to do much, since we have the stuff ready anyway...
            return newProfile;
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
            return newProfile;
        } catch (ParseException e) {
            System.out.println("Failed to sign guest up!");
            return null;
        }
    }

    public static User signUp(String username, String password) {
        return signUp(username, password, null);
    }

}

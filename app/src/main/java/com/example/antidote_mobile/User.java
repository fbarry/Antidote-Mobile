package com.example.antidote_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("unused")
public class User extends ParseUser implements Serializable {

    public User() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof User) {
            return ((User) o).getObjectId().equals(getObjectId());
        }

        return false;
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

    public void incrementNumberOfWins() {
        setNumberOfWins(getNumberOfWins() + 1);
    }

    public int getNumberOfLoses() {
        return this.getInt("numberOfLoses");
    }

    public void setNumberOfLoses(int numberOfLoses) {
        this.put("numberOfLoses", numberOfLoses);
    }

    public void incrementNumberOfLoses() {
        setNumberOfLoses(getNumberOfLoses() + 1);
    }

    public int getNumberOfGames() {
        return getNumberOfWins() + getNumberOfLoses();
    }

    public String getStatus() {
        return this.getString("statusMessage");
    }

    public void setStatus(String status) {
        this.put("statusMessage", status);
    }

    public ArrayList<String> getFriends() {
        //noinspection unchecked
        ArrayList<String> ret = (ArrayList<String>) this.get("friends");
        if (ret != null) return ret;
        else return new ArrayList<>();
    }

    public double getWinRate() {
        if (getNumberOfGames() == 0)
            return 0;
        return (Math.round(((this.getNumberOfWins() * 100.0) / this.getNumberOfGames()) * 100.0) / 100.0);
    }

    public String getStats() {
        return "Games Played: " + getNumberOfGames() + "\n" +
                "Games Won: " + getNumberOfWins() + "\n" +
                "Games Lost: " + getNumberOfLoses() + "\n" +
                "Win Rate: " + getWinRate() + "%";
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

    public static void logoutCurrentUser(Activity activity) {
        SharedPreferences sp;
        sp = activity.getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putBoolean("logged", false).apply();
        sp.edit().putString("currentUser", "ERROR: NOT SET").apply();
        AntidoteMobile.currentUser = null;
        ParseUser.logOutInBackground();
    }
}

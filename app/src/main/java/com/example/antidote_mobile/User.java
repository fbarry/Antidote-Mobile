package com.example.antidote_mobile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

    public void setStats(String id) {
        put("stats", id);
    }

    public Stats getStats() {
        String stats = this.getString("stats");

        Stats ret = null;
        try {
            ParseQuery<ParseObject> query = new ParseQuery<>("Stats");
            ret = (Stats) query.get(stats);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean isGuest() {
        return this.getBoolean("isGuest");
    }

    public void setIsGuest(boolean isGuest) {
        this.put("isGuest", isGuest);
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

    public void removeFriend(String userId) {
        ArrayList<String> friends = getFriends();
        friends.remove(userId);
        this.put("friends", friends);
        saveInBackground();
    }

    public void addFriend(String userId) {
        if (isGuest()) return;

        ArrayList<String> friends = getFriends();
        if (!friends.contains(userId)) friends.add(userId);
        this.put("friends", friends);
        saveInBackground();
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

        Stats stats = new Stats();
        try {
            stats.save();
        } catch (ParseException e) {
            return null;
        }

        newProfile.setStats(stats.getObjectId());

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

        Stats stats = new Stats();
        try {
            stats.save();
        } catch (ParseException e) {
            System.out.println("NO STATS MADE :(");
            e.printStackTrace();
            return null;
        }

        newProfile.setStats(stats.getObjectId());

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

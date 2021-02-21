package com.example.antidote_mobile;

import android.provider.ContactsContract;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;

public class Player {
    String objectId;
    User user;
    ArrayList<String> cards; // Might be changed to Card or something
    int points;

    public Player(){
        cards = new ArrayList<>();
    }

    public Player(ParseObject po) {
        objectId = po.getObjectId();
        points = po.getInt("points");
        //noinspection unchecked
        cards = (ArrayList<String>) po.get("cards");
        ParseQuery<ParseObject> query = new ParseQuery<>("user");
        query.getInBackground(po.getString("who"), (object, e) -> user = new User((ParseUser)object));
    }

    public Player createPlayer(User user) {
        if(user.isGuest){
            User signedup = User.signUpGuest(user.username, AntidoteMobile.guestPassword);

            if (signedup == null) {
                System.out.println("FAILED TO CREATE SIGN UP");
                return null;
            }

            user.objectId = signedup.objectId;
            System.out.println("SIGNED UP: " + user.objectId);
        }

        ParseObject po = new ParseObject("Player");
        po.put("who", user.objectId);
        po.put("cards", new ArrayList<String>());
        po.put("points", 0);

        try {
            po.save();
            objectId = po.getObjectId();
            points = 0;
            cards = new ArrayList<>();
            return this;
        } catch (ParseException e) {
            return null;
        }
    }

}

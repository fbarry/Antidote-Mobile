package com.example.antidote_mobile;

import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class Player {
    String objectId;
    User user;
    ArrayList<String> cards; // Might be changed to Card or something
    int points;

    public Player(){
        cards = new ArrayList<>();
    }

    public Player(ParseObject po){
        objectId = po.getObjectId();
        points = po.getInt("points");
        //noinspection unchecked
        cards = (ArrayList<String>) po.get("cards");
        ParseQuery<ParseObject> query = new ParseQuery<>("user");
        query.getInBackground(po.getString("who"), (object, e) -> user = new User(object));
    }

}

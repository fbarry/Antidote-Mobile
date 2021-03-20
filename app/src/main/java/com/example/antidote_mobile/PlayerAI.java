package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ParseClassName("PlayerAI")
public class PlayerAI extends Player implements Serializable {

    public static final int EASY = 100;
    public static final int MEDIUM = 200;
    public static final int HARD = 300;

    public PlayerAI() {
        super();
    }

    public int getDifficulty() {
        return this.getInt("difficulty");
    }

    public void setDifficulty(int difficulty) {
        this.put("difficulty", difficulty);
    }

    public static String getRandomAIName() {
        Random rand = new Random();
        String[] adj = Utilities.adj;
        String[] noun = Utilities.noun;
        String ret = "robo_" + noun[rand.nextInt(noun.length)] + "_";
        ret += Utilities.getRandomNumberString(AntidoteMobile.maxUsernameLength - ret.length());
        return ret;
    }

    public static Player createPlayerAI() {

        Player ret = Player.createPlayer(User.signUpGuest(), false);

        if (ret == null) return null;

        ret.setUsername(getRandomAIName());
        ret.setIsAI(true);

        try {
            ret.save();
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

}
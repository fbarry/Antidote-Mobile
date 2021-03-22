package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

@ParseClassName("PlayerAI")
public class PlayerAI extends Player implements Serializable {

    enum DIFFICULTY {
        EASY,
        MEDIUM,
        HARD
    }

    public final static int numDifficulties = 3;

    public PlayerAI() {
        super();
    }

    public static String getRandomAIName() {
        Random rand = new Random();
        String[] noun = Utilities.noun;
        String ret = "robo_" + noun[rand.nextInt(noun.length)] + "_";
        ret += Utilities.getRandomNumberString(AntidoteMobile.maxUsernameLength - ret.length());
        return ret;
    }

    public static Player createPlayerAI() {

        Player ret = Player.createPlayer(Objects.requireNonNull(User.signUpGuest()), false);

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
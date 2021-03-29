package com.example.antidote_mobile;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;

public class AntidoteMobile extends Application {
    // Initializes Parse SDK as soon as the application is created

    public static User currentUser;
    public static final String guestPassword = "guestPass1337!_mobileAntidoteMobile";
    public static final int gameCodeLength = 6, maxUsernameLength = 11;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(Player.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(PlayerAI.class);
        Player.registerSubclass(PlayerAI.class);
        ParseObject.registerSubclass(Stats.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("N2wLs7jtNJi8DADzehiIFR5kk5cnoh9zBI9V5uE0")
                .clientKey("5pEyV2pTxhLTLEdR8Pc6Mu3gSWOrvlON8N9sNK5g")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

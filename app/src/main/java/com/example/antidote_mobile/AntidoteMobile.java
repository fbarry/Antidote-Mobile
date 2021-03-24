package com.example.antidote_mobile;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;

public class AntidoteMobile extends Application {
    // Initializes Parse SDK as soon as the application is created

    public static User currentUser;
    public static final String guestPassword = "guestPass1337!_mobileAntidoteMobile";
    public static final int gameCodeLength = 6, maxUsernameLength = 12;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Game.class);
        ParseObject.registerSubclass(Player.class);
        ParseObject.registerSubclass(Message.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("57qfnC7TPZyHUCEbGJiFZ8u2qN7Pr8qqWDu51yxI")
                .clientKey("lsS9eQ0ZUXrhQdWUQnMHGhfNB12mUbjZ1KE8874m")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

package com.example.antidote_mobile;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Application;

public class AntidoteMobile extends Application {
    // Initializes Parse SDK as soon as the application is created

    public static User currentUser;
    public static final String guestPassword = "guestPass1337!_mobileAntidoteMobile";
    public static final int gameCodeLength = 6;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseUser.registerSubclass(User.class);
        ParseObject.registerSubclass(Player.class);
        ParseObject.registerSubclass(Game.class);

//        Parse.initialize(new Parse.Configuration.Builder(this)
//                .applicationId("eTrWlVXg4AnZU2ZMLg7YVsQ6LsgNluEkY9Z7DRob")
//                .clientKey("THyrLzi4oRMpVB0VTc7sG2HlzcSyQT0SsCUeBIBd")
//                .server("https://parseapi.back4app.com")
//                .build()
//        );

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("vyp5dLVIrJQ81Bmy8MBK47rxhJ46hrIT34Xy1yGk")
                .clientKey("Movx5aZHHDMIKPOM3tZNMROrfuG8lladrcFRse7x")
                .server("https://parseapi.back4app.com")
                .build());

        ParseUser.logOutInBackground();

        currentUser = User.getNewGuest();
    }
}

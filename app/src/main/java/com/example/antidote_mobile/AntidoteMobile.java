package com.example.antidote_mobile;

import com.parse.Parse;
import android.app.Application;

public class AntidoteMobile extends Application {
    // Initializes Parse SDK as soon as the application is created

    public static User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("eTrWlVXg4AnZU2ZMLg7YVsQ6LsgNluEkY9Z7DRob")
                .clientKey("THyrLzi4oRMpVB0VTc7sG2HlzcSyQT0SsCUeBIBd")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}

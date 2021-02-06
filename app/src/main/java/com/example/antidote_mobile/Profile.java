package com.example.antidote_mobile;

public class Profile {
    private String username;
    private String password;
    private boolean isGuest = true;

    public Profile() {
    }

    public Profile(String username, String password) {
        this.username = username;
        this.password = password;
        this.isGuest = true;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public boolean getGuestStatus() {
        return this.isGuest;
    }
}

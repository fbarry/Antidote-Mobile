package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Stats")
public class Stats extends ParseObject {

    public Stats() {
        super();
    }

    public int getWins() { return this.getInt("numWins"); }

    public int getLosses() { return this.getInt("numLosses"); }

    public int getNumGames() { return getWins() + getLosses(); }

    public void incrementWins() { this.put("numWins", getWins() + 1);}

    public void incrementLosses() { this.put("numLosses", getLosses() + 1);}

    public double getWinRate() {
        if (getNumGames() == 0)
            return 0;
        return (Math.round(((this.getWins() * 100.0) / this.getNumGames()) * 100.0) / 100.0);
    }

    public String getStats() {
        return "Games Played: " + getNumGames() + "\n" +
                "Games Won: " + getWins() + "\n" +
                "Games Lost: " + getLosses() + "\n" +
                "Win Rate: " + getWinRate() + "%";
    }
}

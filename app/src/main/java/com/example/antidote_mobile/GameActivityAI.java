package com.example.antidote_mobile;

import java.util.Random;

public class GameActivityAI extends GameActivity {

    public GameActivityAI() {
        super();
    }

    public void doAllActions() {
        makeAction();
        selectCards();
    }

    void makeAction() {
        for (Player p : players) {
            if (!p.isAI()) continue;
            if (!currentPlayer.equals(p)) continue;
            Random rand = new Random();

            switch (rand.nextInt(4)) {
                case 0:
                    passCardsLeft(null);
                    break;

                case 1:
                    passCardsRight(null);
                    break;

                case 2:
                    discardCards(null);
                    break;

                case 3:
                    syringeButton(null);
                    break;
            }
        }
    }

    void selectCards() {
        for (Player p : players) {
            if (!p.isAI()) continue;
        }
    }
}

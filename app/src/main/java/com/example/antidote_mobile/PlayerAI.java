package com.example.antidote_mobile;

import com.parse.ParseClassName;
import com.parse.ParseException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
@ParseClassName("PlayerAI")
public class PlayerAI extends Player implements Serializable {

    enum Difficulty {
        EASY(1),
        MEDIUM(2),
        HARD(3),
        NONE(-1);

        private final int diffVal;

        Difficulty(int val) {
            diffVal = val;
        }

        public int getDiffVal() {
            return diffVal;
        }

        static Difficulty fromDiffVal(int val) {
            for (Difficulty t : Difficulty.values()) {
                if (t.diffVal == val) {
                    return t;
                }
            }
            return NONE;
        }

        public Difficulty next() {
            int x = getDiffVal();
            x = (x % Difficulty.values().length) + 1;
            return fromDiffVal(x);
        }

    }

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
        ret.setDifficulty(Difficulty.EASY);

        try {
            ret.save();
            return ret;
        } catch (ParseException e) {
            return null;
        }
    }

    public static void selectPassCard(Player p, Game game) {
        switch (p.difficulty()) {
            case EASY:
                selectPassCardEasy(p, game);
                return;
            case MEDIUM:
                selectPassCardMedium(p, game);
                return;
            case HARD:
                selectPassCardHard(p, game);
                return;
            default:
        }
    }

    private static void selectPassCardEasy(Player p, Game game) {
        // Pick a random card to pass
        int toSelect = Utilities.getRandomInt(0, p.cards().size() - 1);
        p.setSelectedIdx(toSelect);
        p.setIsLocked(true);
    }

    private static void selectPassCardMedium(Player p, Game game) {
        // Pick a random non-toxin, non-syringe card to pass
        ArrayList<Integer> candidates = new ArrayList<>();

        ArrayList<String> cardStrings = p.cards();
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.ANTIDOTE)
                candidates.add(i);
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // Oops, we only have interesting cards, pass a syringe if we can
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        // We only have toxins! who cares at this point
        p.setSelectedIdx(Utilities.getRandomInt(0, cardStrings.size() - 1));
        p.setIsLocked(true);
    }

    private static void selectPassCardHard(Player p, Game game) {
        // TODO: implement
        selectPassCardMedium(p, game);
    }

    public static void selectTradeCard(Player p, Game game) {
        switch (p.difficulty()) {
            case EASY:
                selectTradeCardEasy(p, game);
                return;
            case MEDIUM:
                selectTradeCardMedium(p, game);
                return;
            case HARD:
                selectTradeCardHard(p, game);
                return;
            default:
        }
    }

    private static void selectTradeCardEasy(Player p, Game game) {
        // Aim to be the most helpful, even at our own expense
        // Toxin > Syringe > Useful, high-numbered antidote > Useful, low-numbered antidote > useless antidote
        // Pick a random toxin
        ArrayList<Integer> candidates = new ArrayList<>();

        ArrayList<String> cardStrings = p.cards();
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.TOXIN)
                candidates.add(i);
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // Oops, we don't have a toxin, pass a syringe if we can
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        HashSet<Toxin> seenToxins = p.getRememberedToxins();

        // We only have antidotes! Pick the highest number we don't remember
        for (int numberTarget = game.numPlayers(); numberTarget > 0; numberTarget--) {
            for (int i = 0; i < cardStrings.size(); i++) {
                if (seenToxins.contains(Card.getToxin(cardStrings.get(i)))) continue;
                if (Card.getNumber(cardStrings.get(i)) != numberTarget) continue;
                candidates.add(i);
            }
            if (candidates.size() != 0) {
                p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
                p.setIsLocked(true);
                return;
            }
        }

        // Darn, we only have useless antidotes...
        p.setSelectedIdx(Utilities.getRandomInt(0, cardStrings.size() - 1));
        p.setIsLocked(true);
    }

    private static void selectTradeCardMedium(Player p, Game game) {
        // Aim to be somewhat helpful (Syringe > Toxin > Any antidote)
        ArrayList<String> cardStrings = p.cards();

        // Trade a syringe, if we can
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        // Pick a random toxin
        ArrayList<Integer> candidates = new ArrayList<>();

        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.TOXIN)
                candidates.add(i);
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // We only have antidotes! Pick a random one
        p.setSelectedIdx(Utilities.getRandomInt(0, cardStrings.size() - 1));
        p.setIsLocked(true);
    }

    private static void selectTradeCardHard(Player p, Game game) {
        // TODO: implement
        selectTradeCardMedium(p, game);
    }

    public static void selectDiscardCard(Player p, Game game) {
        switch (p.difficulty()) {
            case EASY:
                selectDiscardEasy(p, game);
                return;
            case MEDIUM:
                selectDiscardMedium(p, game);
                return;
            case HARD:
                selectDiscardHard(p, game);
                return;
            default:
        }
    }

    private static void selectDiscardEasy(Player p, Game game) {
        // Discard a random card
        int toSelect = Utilities.getRandomInt(0, p.cards().size() - 1);
        p.setSelectedIdx(toSelect);
        p.setIsLocked(true);
    }

    private static void selectDiscardMedium(Player p, Game game) {
        // Discard a toxin, then a useless antidote, then a syringe, then a useful antidote
        ArrayList<String> cardStrings = p.cards();

        // Trade a syringe, if we can
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.TOXIN) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }


        HashSet<Toxin> seenToxins = p.getRememberedToxins();
        ArrayList<Integer> candidates = new ArrayList<>();

        // Pick a random useless antidote
        for (int i = 0; i < cardStrings.size(); i++) {
            if (seenToxins.contains(Card.getToxin(cardStrings.get(i)))) continue;
            candidates.add(i);
        }
        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // Pick a syringe
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        // We only have useful antidotes! Pick a random one
        p.setSelectedIdx(Utilities.getRandomInt(0, cardStrings.size() - 1));
        p.setIsLocked(true);
    }

    private static void selectDiscardHard(Player p, Game game) {
        // TODO: implement
        selectDiscardMedium(p, game);
    }




}
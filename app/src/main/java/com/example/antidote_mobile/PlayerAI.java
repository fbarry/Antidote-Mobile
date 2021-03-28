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
            x = (x % (Difficulty.values().length - 1)) + 1;
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

    private static HashSet<Toxin> getPossibleToxins(Player p, Game game) {

        HashSet<Toxin> allToxins = new HashSet<>();

        for (Toxin t : Toxin.values()) {
            if (t == Toxin.AGENTU && game.numPlayers() != 7) continue;
            allToxins.add(t);
        }

        allToxins.removeAll(p.getRememberedToxins());

        return allToxins;
    }

    private static boolean hasAntidote(Player p, Game game, Toxin t) {
        return getMatchingAntidotes(p, game, t).size() > 0;
    }

    private static ArrayList<String> getMatchingAntidotes(Player p, Game game, Toxin t) {
        ArrayList<String> cards = p.cards();

        ArrayList<String> match = new ArrayList<>();

        for (String card : p.cards()) {
            if (Card.getCardType(card) == CardType.ANTIDOTE) {
                if (Card.getToxin(card) == t) {
                    match.add(card);
                }
            }
        }

        return match;
    }

    private static Toxin getRandomPossibleToxin(Player p, Game game) {
        HashSet<Toxin> possibleToxins = getPossibleToxins(p, game);

        return possibleToxins.toArray(new Toxin[0])[Utilities.getRandomInt(0, possibleToxins.size() - 1)];
    }

    private static ArrayList<String> getAllToxinsInHand(Player p, Game game) {
        ArrayList<String> toxins = new ArrayList<>();

        for (String card : p.cards()) {
            if (Card.getCardType(card) == CardType.TOXIN) {
                toxins.add(card);
            }
        }

        return toxins;
    }

    public static void selectAction(Player p, Game game) {
        switch (p.difficulty()) {
            case EASY:
                selectActionEasy(p, game);
                return;
            case MEDIUM:
                selectActionMedium(p, game);
                return;
            case HARD:
                selectActionHard(p, game);
                return;
            default:
        }
    }

    private static void selectRandomPass(Player p, Game game) {
        int choice = Utilities.getRandomInt(0, 1);
        switch (choice) {
            case 0:
                game.setCurrentAction(ActionType.PASSLEFT);
                return;
            case 1:
                game.setCurrentAction(ActionType.PASSRIGHT);
                return;
            default:
                game.setCurrentAction(ActionType.DISCARD);
        }
    }

    private static void selectActionEasy(Player p, Game game) {
        // Pass left or right, or discard if it doesn't end the game
        int choice = Utilities.getRandomInt(0, 2);
        if (game.numCards() == 2) choice = Utilities.getRandomInt(0, 1);

        switch (choice) {
            case 0:
                game.setCurrentAction(ActionType.PASSLEFT);
                return;
            case 1:
                game.setCurrentAction(ActionType.PASSRIGHT);
                return;
            case 2:
            default:
                game.setCurrentAction(ActionType.DISCARD);
        }
    }

    private static void selectActionMedium(Player p, Game game) {
        HashSet<Toxin> possibleToxins = getPossibleToxins(p, game);

        if (possibleToxins.size() == 1) {

            if (hasAntidote(p, game, getRandomPossibleToxin(p, game))) {
                // If has antidote, end game ASAP
                game.setCurrentAction(ActionType.DISCARD);
                return;
            }
        }

        selectRandomPass(p, game);

    }

    private static void selectActionHard(Player p, Game game) {
        // This AI is just going to cheat

        Toxin trueToxin = game.toxin();

        if (hasAntidote(p, game, trueToxin)) {
            // If has antidote, end game ASAP
            game.setCurrentAction(ActionType.DISCARD);
            return;
        }

        // If doesn't have antidote, pick an action randomly depending on the number of cards left and whether it has a toxin
        // Fewer cards = more likely to pass to stall game
        // More cards & more toxins = more likely to discard to deny information

        int numToxins = getAllToxinsInHand(p, game).size();

        if (numToxins == 0) {
            selectRandomPass(p, game);
            return;
        }

        int choice = Utilities.getRandomInt(3, 10);

        if (choice >= p.cards().size()) {
            selectRandomPass(p, game);
            return;
        }

        choice = Utilities.getRandomInt(0, p.cards().size() - 1);

        if (choice < numToxins) {
            game.setCurrentAction(ActionType.DISCARD);
        } else {
            selectRandomPass(p, game);
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
        // Pick a random antidote card to pass
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
        Toxin trueToxin = game.toxin();

        // Pick random antidote for not the real toxin
        ArrayList<Integer> candidates = new ArrayList<>();

        ArrayList<String> cardStrings = p.cards();
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.ANTIDOTE)
                if (Card.getToxin(cardStrings.get(i)) != trueToxin)
                    candidates.add(i);
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // Pass a syringe otherwise
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        // Okay even passing a toxin is fine
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.TOXIN) {
                p.setSelectedIdx(i);
                p.setIsLocked(true);
                return;
            }
        }

        // Okay we only have the true antidote; time to pass the lowest value one

        int lowestValue = 8;
        int lowestIndex = 0;

        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.ANTIDOTE) {
                if (Card.getNumber(cardStrings.get(i)) < lowestValue) {
                    lowestValue = Card.getNumber(cardStrings.get(i));
                    lowestIndex = i;
                }
            }
        }

        p.setSelectedIdx(lowestIndex);
        p.setIsLocked(true);
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
        // Same rules as passing cards; aims to sabotage other players the most
        selectPassCardHard(p, game);
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

        // Discard a toxin if we can
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
        System.out.println(cardStrings + " from " + p.username());
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.SYRINGE) continue;
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
        Toxin trueToxin = game.toxin();

        ArrayList<Integer> candidates = new ArrayList<>();

        ArrayList<String> cardStrings = p.cards();

        // We want to discard a random true antidote if we have multiples, as long as it isn't our highest value one
        int highestValue = 0;
        for (String card : cardStrings) {
            if (Card.getCardType(card) == CardType.ANTIDOTE) {
                if (Card.getToxin(card) == trueToxin) {
                    highestValue = Math.max(highestValue, Card.getNumber(card));
                }
            }
        }

        for (int i = 0; i < cardStrings.size(); i++) {
            String card = cardStrings.get(i);
            if (Card.getCardType(card) == CardType.ANTIDOTE) {
                if (Card.getToxin(card) == trueToxin) {
                    if (Card.getNumber(card) < highestValue) {
                        candidates.add(i);
                    }
                }
            }
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // We want to discard a random toxin
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.TOXIN) {
                candidates.add(i);
            }
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // Pick a random non-true antidote
        for (int i = 0; i < cardStrings.size(); i++) {
            if (Card.getCardType(cardStrings.get(i)) == CardType.ANTIDOTE) {
                if (Card.getToxin(cardStrings.get(i)) != trueToxin) {
                    candidates.add(i);
                }
            }
        }

        if (candidates.size() != 0) {
            p.setSelectedIdx(candidates.get(Utilities.getRandomInt(0, candidates.size() - 1)));
            p.setIsLocked(true);
            return;
        }

        // If none of those work, we just have syringes, so just discard a random card
        p.setSelectedIdx(Utilities.getRandomInt(0, cardStrings.size() - 1));
        p.setIsLocked(true);
    }

}
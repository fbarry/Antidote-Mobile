package com.example.antidote_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity implements ChatDialogActivity {

    @SuppressWarnings("unused")
    public static final int millisPerUpdate = 4_000;
    Timer refreshTimer;
    Game game;
    Player currentPlayer;
    CardHandler ch;
    ArrayList<Player> players;
    ImageButton chatButton;
    ChatDialog chatDialog;
    int ourIdx = 0;
    boolean refreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        if (!currentPlayer.isHost()) {
            Button endGameButton = findViewById(R.id.deleteGameInGame);
            endGameButton.setVisibility(View.GONE);
        }

        hideEverything();

        chatButton = findViewById(R.id.chatButtonGame);

        chatDialog = new ChatDialog(GameActivity.this, game.getObjectId(), currentPlayer.username());
        chatDialog.create();

        initializePlayers();

        for (int i = 0; i < game.numPlayers(); i++) {
            getPlayerConfirmed(i).setVisibility(View.VISIBLE);
            getPlayerWorkstation(i).setVisibility(View.VISIBLE);
            getPlayerTextView(i).setVisibility(View.VISIBLE);
            getPlayerTextView(i).setText(players.get(i).username().replace("(Host)", ""));
        }
        getPlayerTextView(ourIdx).append(" (You)");

        ch = findViewById(R.id.cardHandler);
        ch.setCards(currentPlayer.cards());

        ch.setValueChangeListener(() -> {
            if (game.currentActionType() == ActionType.SYRINGE) {
                if (ch.lifted == null || ch.lifted.type != CardType.SYRINGE) {
                    game.setCurrentAction(ActionType.NONE);
                    updateTurnTextView();
                    updateActionVisibilities();
                }
            } else {
                if (ch.lifted != null && game.currentActionType() != ActionType.NONE && currentlyTrading()) {
                    // We have selected a card (possibly just selected it)
                    findViewById(R.id.confirmButton).setVisibility(View.VISIBLE);
                } else {
                    // We have not selected a card (possibly just deselected it)
                    findViewById(R.id.confirmButton).setVisibility(View.GONE);
                }
            }
        });

        TextView turnTextView = findViewById(R.id.turnTextView);
        turnTextView.append(" " + currentPlayer.username());

        refreshTimer = new Timer();

        update();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                runOnUiThread(() -> update());
            }
        }, 0, millisPerUpdate);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        refreshTimer.cancel();
    }

    public void initializePlayers() {
        players = new ArrayList<>();
        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());
        try {
            List<ParseObject> parseObjects = getPlayers.find();
            for (String pid : game.players()) {
                for (ParseObject currObject : parseObjects) {
                    if (currObject.getObjectId().equals(pid)) {
                        players.add((Player) currObject);
                        break;
                    }
                }
            }
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                ourIdx = i;
                if (p.getObjectId().equals(currentPlayer.getObjectId())) {
                    currentPlayer = p;
                    break;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public boolean currentlyTrading() {
        if (game.currentActionType() != ActionType.TRADE) return true;
        boolean ans = players.get(game.currentTurn()).getObjectId().equals(currentPlayer.getObjectId());
        if (game.tradeTarget() != -1)
            ans |= players.get(game.tradeTarget()).getObjectId().equals(currentPlayer.getObjectId());
        return ans;
    }

    public void update() {
        if (game == null) return;
        refreshing = true;
        updateGame();
        updateChat();
    }

    public void updateChat() {
        if (game == null) return;
        chatDialog.refreshMessages();
    }

    public void showChatNotification() {
        if (!chatDialog.isShowing()) {
            chatButton.setImageResource(R.drawable.ic_baseline_mark_chat_unread_24);
        }
    }

    public void onClickChat(View v) {
        launchChatPopup();
        chatButton.setImageResource(R.drawable.ic_baseline_chat_bubble_24);
    }

    public void launchChatPopup() {
        chatDialog.show();
    }

    public void updateGame() {
        ParseQuery.getQuery("Game").getInBackground(game.getObjectId(), (object, e) -> {
            game = (Game) object;

            if (game == null) {
                Utilities.showInformationAlert(this,
                        R.string.game_ended_by_host,
                        R.string.sorry,
                        (dialog, which) -> GameActivity.this.finish());
                currentPlayer = null;
                return;
            }

            updatePlayers();
        });
    }

    public void updatePlayers() {
        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());
        System.out.println(game.players());

        try {
            List<ParseObject> parseObjects = getPlayers.find();
            System.out.println("Got " + parseObjects.size() + " updated players, previously had " + players.size());
            for (int i = 0; i < players.size(); i++) {

                Player p = (Player) parseObjects.get(i);

                for (int j = 0; j < players.size(); j++) {
                    if (parseObjects.get(i).getObjectId().equals(players.get(j).getObjectId())) {
                        players.set(j, p);
                        if (players.get(j).getObjectId().equals(currentPlayer.getObjectId())) {
                            updateCurrentPlayer(players.get(j));
                        }
                        break;
                    }
                }
            }


            ch.setCrossToxins(currentPlayer.getRememberedToxins());

            updateActionVisibilities();

            if (currentPlayer.needsGameOverScreen()) {
                goToGameOverActivity();
            }

            if (currentPlayer.isHost()) {
                computeTurn();
            }

            updateConfirmedDisplays();

            updateTurnTextView();

            updateActionDetails();

            if (players.get(game.currentTurn()).isAI() && game.currentActionType() == ActionType.NONE) {
                PlayerAI.selectAction(players.get(game.currentTurn()), game);
                game.saveInBackground(e -> update());
            }
            refreshing = false;
        } catch (ParseException ignored) {
            refreshing = false;
        }
    }

    public void goToGameOverActivity() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameInfo", game);
        bundle.putSerializable("currentPlayer", currentPlayer);
        bundle.putSerializable("players", players);
        Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        GameActivity.this.finish();
    }

    public void endGame() {
        for (Player p : players) {
            p.setLastRoundPoints(p.calculatePoints(game.toxin()));
            p.setPoints(p.points() + p.lastRoundPoints());
            p.setNeedsGameOverScreen(true);
        }
        game.setNumRoundsCompleted(game.numRoundsCompleted() + 1);
        game.setNumCards(0);
    }

    public void onClickEndGame(View v) {
        Utilities.showConfirmationAlert(this,
                "Are you sure you want to end this game?",
                "You cannot undo this action.",
                (dialog, which) -> {
                    while (refreshing) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    game.deleteGame();
                    currentPlayer = null;
                    game = null;
                    GameActivity.this.finish();
                });
    }


    public void computeTurn() {
        int numLocked = 0;
        int numAi = 0;
        for (Player p : players) {
            if (p.isLocked()) numLocked++;
            else if (p.isAI()) numAi++;
        }
        System.out.println(numLocked + " players were locked, out of " + game.numPlayers());
        if (numLocked + numAi == game.numPlayers()) {
            switch (ActionType.fromString(game.currentAction())) {
                case PASSLEFT:
                    performPassLeft();
                    break;
                case PASSRIGHT:
                    performPassRight();
                    break;
                case DISCARD:
                    performDiscard();
                    break;
                case NONE:
                default:
            }
        }

        if (game.currentActionType() == ActionType.TRADE && game.tradeTarget() != -1) {
            boolean traderConfirmed = players.get(game.currentTurn()).isAI() || players.get(game.currentTurn()).isLocked();
            boolean tradeeConfirmed = players.get(game.tradeTarget()).isAI() || players.get(game.tradeTarget()).isLocked();
            if (tradeeConfirmed && traderConfirmed) performTrade();
        }
    }

    void updateConfirmedDisplays() {
        if (game.currentActionType() == ActionType.NONE || game.currentActionType() == ActionType.SYRINGE) {
            for (int i = 0; i < game.numPlayers(); i++) {
                getPlayerConfirmed(i).setVisibility(View.GONE);
            }
            return;
        }

        if (game.currentActionType() == ActionType.TRADE) {
            for (int i = 0; i < game.numPlayers(); i++)
                getPlayerConfirmed(i).setVisibility(View.GONE);
            if (players.get(game.currentTurn()).isAI() || players.get(game.currentTurn()).isLocked())
                getPlayerConfirmed(game.currentTurn()).setVisibility(View.VISIBLE);
            if (game.tradeTarget() != -1 && (players.get(game.tradeTarget()).isLocked() || players.get(game.tradeTarget()).isAI()))
                getPlayerConfirmed(game.tradeTarget()).setVisibility(View.VISIBLE);
            return;
        }

        switch (game.numPlayers()) {
            case 7:
                if (players.get(6).isLocked() || (players.get(6).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(6).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(6).setVisibility(View.GONE);
            case 6:
                if (players.get(5).isLocked() || (players.get(5).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(5).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(5).setVisibility(View.GONE);
            case 5:
                if (players.get(4).isLocked() || (players.get(4).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(4).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(4).setVisibility(View.GONE);
            case 4:
                if (players.get(3).isLocked() || (players.get(3).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(3).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(3).setVisibility(View.GONE);
            case 3:
                if (players.get(2).isLocked() || (players.get(2).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(2).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(2).setVisibility(View.GONE);
            case 2:
                if (players.get(1).isLocked() || (players.get(1).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(1).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(1).setVisibility(View.GONE);
            case 1:
                if (players.get(0).isLocked() || (players.get(0).isAI() && game.currentActionType() != ActionType.TRADE))
                    getPlayerConfirmed(0).setVisibility(View.VISIBLE);
                else getPlayerConfirmed(0).setVisibility(View.GONE);
        }
        for (Player p : players) System.out.println(p.isLocked());
    }

    void updateCurrentPlayer(Player newCurrentPlayer) {
        // Update currentPlayer and the game's UI
        ArrayList<String> oldCards = currentPlayer.cards();
        currentPlayer = newCurrentPlayer;
        if (!oldCards.equals(ch.getCardData())) {
            ch.setCards(currentPlayer.cards());
            System.out.println("Updated cards!");
        }
        Drawable nimg;
        Context context = findViewById(R.id.confirmButton).getContext();
        if (currentPlayer.isLocked()) {
            ch.forceAll();
            ch.forceSelect(currentPlayer.selectedIdx());
            nimg = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_cancel_24);
        } else {
//            ch.deselect();
            nimg = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_check_circle_24);
        }
        ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
        ch.selectable = !currentPlayer.isLocked();
    }

    void setActionDetails() {
        findViewById(R.id.actionDetails).setVisibility(TextView.VISIBLE);
        ch.setActionDetailsNeeded();
    }

    void setNoActionDetails() {
        findViewById(R.id.actionDetails).setVisibility(TextView.GONE);
        ch.setNoActionDetailsNeeded();
    }

    void updateActionDetails() {
        TextView actionDetails = findViewById(R.id.actionDetails);
        switch (game.currentActionType()) {
            case SYRINGE:
            case NONE:
                setNoActionDetails();
                return;
            case TRADE:
                if (game.tradeTarget() == -1 || !currentlyTrading()) {
                    setNoActionDetails();
                    return;
                }
                actionDetails.setText(R.string.tradeACard);
                break;
            case PASSLEFT:
                actionDetails.setText(R.string.passLeft);
                break;
            case PASSRIGHT:
                actionDetails.setText(R.string.passRight);
                break;
            case DISCARD:
                actionDetails.setText(R.string.discardACard);
                break;
        }
        setActionDetails();
    }

    void updateTurnTextView() {
        TextView turnTextView = findViewById(R.id.turnTextView);
        turnTextView.setText(R.string.turn_);
        turnTextView.append(" " + players.get(game.currentTurn()).username());
        turnTextView.append(", Action: ");
        turnTextView.append(game.currentAction());
        if (game.currentActionType() == ActionType.TRADE)
            if (game.tradeTarget() == -1) turnTextView.append(" with ?");
            else turnTextView.append(" with " + players.get(game.tradeTarget()).username());
    }

    void updateActionVisibilities() {
        if (game.players().get(game.currentTurn()).equals(currentPlayer.getObjectId())
                && game.currentActionType() == ActionType.NONE) {
            findViewById(R.id.passCardsLeftButton).setVisibility(View.VISIBLE);
            findViewById(R.id.syringeButton).setVisibility(currentPlayer.hasSyringe() ? View.VISIBLE : View.GONE);
            findViewById(R.id.discardCardsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.passCardsRightButton).setVisibility(View.VISIBLE);
            findViewById(R.id.tradeButton).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.passCardsLeftButton).setVisibility(View.GONE);
            findViewById(R.id.syringeButton).setVisibility(View.GONE);
            findViewById(R.id.discardCardsButton).setVisibility(View.GONE);
            findViewById(R.id.passCardsRightButton).setVisibility(View.GONE);
            findViewById(R.id.tradeButton).setVisibility(View.GONE);
        }
    }

    void finalizeAction() {
        game.setCurrentTurn((game.currentTurn() + 1) % game.numPlayers());
        game.setCurrentAction(ActionType.NONE.getText());
        game.setNumCards(currentPlayer.cards().size());
        for (Player p : players) p.rememberToxinsInHand();

        if (players.get(game.currentTurn()).isAI())
            PlayerAI.selectAction(players.get(game.currentTurn()), game);


        ParseObject.saveAllInBackground(players, e1 -> game.saveInBackground((e2 -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            update();
        })));
    }

    // 1 for left, -1 for right
    void performPass(int direction) {
        for (Player p : players) if (p.isAI()) PlayerAI.selectPassCard(p, game);
        ArrayList<String> trades = new ArrayList<>();
        for (Player p : players) trades.add(p.cards().get(p.selectedIdx()));
        for (int i = 0; i < game.numPlayers(); i++) {
            ArrayList<String> pCards = players.get(i).cards();
            pCards.remove(trades.get(i));
            int gidx = (trades.size() + i + direction) % trades.size();
            pCards.add(trades.get(gidx));
            players.get(i).setCards(pCards);
            players.get(i).deselect();
        }
        finalizeAction();
    }

    void performPassLeft() {
        System.out.println("Passing Left!!!");
        performPass(1);
    }

    void performPassRight() {
        System.out.println("Passing Right!!!");
        performPass(-1);
    }

    void performDiscard() {
        System.out.println("Discarding!!!");
        for (Player p : players) {
            if (p.isAI()) PlayerAI.selectDiscardCard(p, game);
            ArrayList<String> pCards = p.cards(), pWorkstation = p.workstation();
            pWorkstation.add(pCards.remove(p.selectedIdx()));
            p.setCards(pCards);
            p.setWorkstation(pWorkstation);
            p.deselect();
        }

        game.setNumCards(game.numCards() - 1);

        if (game.numCards() == 1) {
            endGame();
        }

        finalizeAction();
    }

    void performSyringe(int playerIdx, int cardIdx) {
        for (Player p : players) {
            if (p.getObjectId().equals(currentPlayer.getObjectId())) {

                ArrayList<String> otherWorkstation = players.get(playerIdx).workstation();
                ArrayList<String> ourCards = p.cards();
                String fromThem = otherWorkstation.remove(cardIdx);
                String fromUs = ourCards.remove(ch.getSelectedIndex());
                otherWorkstation.add(cardIdx, fromUs);
                ourCards.add(ch.getSelectedIndex(), fromThem);

                players.get(playerIdx).setWorkstation(otherWorkstation);
                p.setCards(ourCards);
            }
        }
        finalizeAction();
    }

    void performTrade() {
        Player us = players.get(game.currentTurn());
        Player them = players.get(game.tradeTarget());

        if (us.isAI()) PlayerAI.selectTradeCard(us, game);
        if (them.isAI()) PlayerAI.selectTradeCard(them, game);

        ArrayList<String> ourCards = us.cards();
        ArrayList<String> theirCards = them.cards();

        String ourSelectedCard = ourCards.remove(us.selectedIdx());
        String theirSelectedCard = theirCards.remove(them.selectedIdx());

        ourCards.add(us.selectedIdx(), theirSelectedCard);
        theirCards.add(them.selectedIdx(), ourSelectedCard);

        us.setCards(ourCards);
        them.setCards(theirCards);

        us.deselect();
        them.deselect();

        game.setTradeTarget(-1);
        finalizeAction();
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(GameActivity.this, InfoPageActivity.class));
    }

    public void viewWorkstation(int playerNum) {
        // playerNum is zero-indexed
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.activity_workstation);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");

        myDialog.show();

        CardHandler workstationCh = myDialog.findViewById(R.id.cardHandlerWorkstation);
        workstationCh.setCards(players.get(playerNum).workstation());
        workstationCh.workstation = true;
        workstationCh.setCrossToxins(currentPlayer.getRememberedToxins());

        TextView whoseTextView = myDialog.findViewById(R.id.whoseWorkstationTextView);
        whoseTextView.setText(players.get(playerNum).username().replace("(Host)", ""));
        if (players.get(playerNum) == currentPlayer)
            whoseTextView.setText(R.string.your_workstation);
        else whoseTextView.append("'s Workstation");


        ImageButton syringeButton = myDialog.findViewById(R.id.confirmSyringeButton);
        syringeButton.setVisibility(View.GONE);

        boolean playerReady = game.currentActionType() == ActionType.SYRINGE && ch.getSelectedIndex() != -1;
        if (playerReady) {
            workstationCh.setValueChangeListener(() -> {
                if (workstationCh.getSelectedIndex() == -1) {
                    syringeButton.setVisibility(View.GONE);
                } else {
                    syringeButton.setVisibility(View.VISIBLE);
                }
            });
            syringeButton.setOnClickListener(v -> {
                performSyringe(playerNum, workstationCh.getSelectedIndex());
                myDialog.dismiss();
            });
        }

    }

    public void postAction(ActionType at) {
        if (currentPlayer.getObjectId().equals(game.players().get(game.currentTurn()))) {
            game.setCurrentAction(at.getText());
            game.saveInBackground(e -> update());
        }
    }

    public void passCardsLeft(View v) {
        postAction(ActionType.PASSLEFT);
    }

    public void passCardsRight(View v) {
        postAction(ActionType.PASSRIGHT);
    }

    public void discardCards(View v) {
        postAction(ActionType.DISCARD);
    }

    public void syringeButton(View v) {
        game.setCurrentAction(ActionType.SYRINGE);
        currentPlayer.setCards(ch.getCardData());
        ch.forceSelect(currentPlayer.cards().indexOf(CardType.SYRINGE.getText()));
        updateTurnTextView();
    }

    public void confirmSelection(View v) {
        int selectedIdx = ch.getSelectedIndex();
        if (selectedIdx == -1) return;
        Drawable nimg;
        Context context = findViewById(R.id.confirmButton).getContext();

        if (!ch.selectable) {
            // we meant to deselect
            ch.deselect();
            ch.selectable = true;
            nimg = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_check_circle_24);
            ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
            currentPlayer.setIsLocked(false);
            currentPlayer.setSelectedIdx(-1);
            currentPlayer.setCards(ch.getCardData());
        } else {
            System.out.println("Selected " + selectedIdx);
            currentPlayer.setIsLocked(true);
            currentPlayer.setCards(ch.getCardData());
            currentPlayer.setSelectedIdx(selectedIdx);
            ch.selectable = false;
            nimg = AppCompatResources.getDrawable(context, R.drawable.ic_baseline_cancel_24);
        }

        ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
        currentPlayer.saveInBackground(e -> {
            try {
                Thread.sleep(200);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            update();
        });
    }

    public void onTradeButton(View view) {
        game.setCurrentAction(ActionType.TRADE);
        game.setTradeTarget(-1);
        game.saveInBackground(e -> update());
    }

    public void onPlayerWorkstation(View view) {
        for (int i = 0; i < game.numPlayers(); i++) {
            if (getPlayerWorkstation(i) == view) {
                viewWorkstation(i);
                return;
            }
        }
    }

    public void onClickPlayer(int idx) {
        if (game.currentActionType() != ActionType.TRADE || game.tradeTarget() != -1) return;
        if (idx == players.indexOf(currentPlayer)) return;
        game.setTradeTarget(idx);
        game.saveInBackground(e -> update());
    }

    public void onClickPlayer(View view) {
        for (int i = 0; i < game.numPlayers(); i++) {
            if (getPlayerTextView(i) == view) {
                onClickPlayer(i);
                return;
            }
        }
    }

    public TextView getPlayerTextView(int playerIdx) {
        int pidx = (playerIdx - ourIdx + game.numPlayers()) % game.numPlayers();
        if (pidx == 0) return findViewById(R.id.player1TextView);

        // even number of players, so we will be using the top
        if (pidx == 1) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.rightPlayer1TextView);
                case 4:
                    return findViewById(R.id.rightPlayer2TextView);
                case 5:
                case 6:
                case 7:
                    return findViewById(R.id.rightPlayer3TextView);
            }
        } else if (pidx == 2) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.leftPlayer1TextView);
                case 4:
                    return findViewById(R.id.topPlayerTextView);
                case 5:
                case 6:
                    return findViewById(R.id.rightPlayer1TextView);
                case 7:
                    return findViewById(R.id.rightPlayer2TextView);
            }
        } else if (pidx == 3) {
            switch (game.numPlayers()) {
                case 4:
                    return findViewById(R.id.leftPlayer2TextView);
                case 5:
                    return findViewById(R.id.leftPlayer1TextView);
                case 6:
                    return findViewById(R.id.topPlayerTextView);
                case 7:
                    return findViewById(R.id.rightPlayer1TextView);
            }
        } else if (pidx == 4) {
            switch (game.numPlayers()) {
                case 5:
                    return findViewById(R.id.leftPlayer3TextView);
                case 6:
                case 7:
                    return findViewById(R.id.leftPlayer1TextView);
            }
        } else if (pidx == 5) {
            switch (game.numPlayers()) {
                case 6:
                    return findViewById(R.id.leftPlayer3TextView);
                case 7:
                    return findViewById(R.id.leftPlayer2TextView);
            }
        } else if (pidx == 6) {
            if (game.numPlayers() == 7) {
                return findViewById(R.id.leftPlayer3TextView);
            }
        }
        System.out.println("No return case in getPlayerTextView, returning null (" + pidx + "," + game.numPlayers() + ")");
        return null;
    }

    public ImageButton getPlayerWorkstation(int playerIdx) {
        int pidx = (playerIdx - ourIdx + game.numPlayers()) % game.numPlayers();
        if (pidx == 0) return findViewById(R.id.player1Workstation);

        // even number of players, so we will be using the top
        if (pidx == 1) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.rightPlayer1Workstation);
                case 4:
                    return findViewById(R.id.rightPlayer2Workstation);
                case 5:
                case 6:
                case 7:
                    return findViewById(R.id.rightPlayer3Workstation);
            }
        } else if (pidx == 2) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.leftPlayer1Workstation);
                case 4:
                    return findViewById(R.id.topPlayerWorkstation);
                case 5:
                case 6:
                    return findViewById(R.id.rightPlayer1Workstation);
                case 7:
                    return findViewById(R.id.rightPlayer2Workstation);
            }
        } else if (pidx == 3) {
            switch (game.numPlayers()) {
                case 4:
                    return findViewById(R.id.leftPlayer2Workstation);
                case 5:
                    return findViewById(R.id.leftPlayer1Workstation);
                case 6:
                    return findViewById(R.id.topPlayerWorkstation);
                case 7:
                    return findViewById(R.id.rightPlayer1Workstation);
            }
        } else if (pidx == 4) {
            switch (game.numPlayers()) {
                case 5:
                    return findViewById(R.id.leftPlayer3Workstation);
                case 6:
                case 7:
                    return findViewById(R.id.leftPlayer1Workstation);
            }
        } else if (pidx == 5) {
            switch (game.numPlayers()) {
                case 6:
                    return findViewById(R.id.leftPlayer3Workstation);
                case 7:
                    return findViewById(R.id.leftPlayer2Workstation);
            }
        } else if (pidx == 6) {
            if (game.numPlayers() == 7) {
                return findViewById(R.id.leftPlayer3Workstation);
            }
        }
        System.out.println("No return case in getPlayerWorkstation, returning null (" + pidx + "," + game.numPlayers() + ")");
        return null;
    }

    public ImageView getPlayerConfirmed(int playerIdx) {
        int pidx = (playerIdx - ourIdx + game.numPlayers()) % game.numPlayers();
        if (pidx == 0) return findViewById(R.id.player1Confirmed);

        // even number of players, so we will be using the top
        if (pidx == 1) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.rightPlayer1Confirmed);
                case 4:
                    return findViewById(R.id.rightPlayer2Confirmed);
                case 5:
                case 6:
                case 7:
                    return findViewById(R.id.rightPlayer3Confirmed);
            }
        } else if (pidx == 2) {
            switch (game.numPlayers()) {
                case 3:
                    return findViewById(R.id.leftPlayer1Confirmed);
                case 4:
                    return findViewById(R.id.topPlayerConfirmed);
                case 5:
                case 6:
                    return findViewById(R.id.rightPlayer1Confirmed);
                case 7:
                    return findViewById(R.id.rightPlayer2Confirmed);
            }
        } else if (pidx == 3) {
            switch (game.numPlayers()) {
                case 4:
                    return findViewById(R.id.leftPlayer2Confirmed);
                case 5:
                    return findViewById(R.id.leftPlayer1Confirmed);
                case 6:
                    return findViewById(R.id.topPlayerConfirmed);
                case 7:
                    return findViewById(R.id.rightPlayer1Confirmed);
            }
        } else if (pidx == 4) {
            switch (game.numPlayers()) {
                case 5:
                    return findViewById(R.id.leftPlayer3Confirmed);
                case 6:
                case 7:
                    return findViewById(R.id.leftPlayer1Confirmed);
            }
        } else if (pidx == 5) {
            switch (game.numPlayers()) {
                case 6:
                    return findViewById(R.id.leftPlayer3Confirmed);
                case 7:
                    return findViewById(R.id.leftPlayer2Confirmed);
            }
        } else if (pidx == 6) {
            if (game.numPlayers() == 7) {
                return findViewById(R.id.leftPlayer3Confirmed);
            }
        }
        System.out.println("No return case in getPlayerConfirmed, returning null (" + pidx + "," + game.numPlayers() + ")");
        return null;
    }

    public void hideEverything() {
        findViewById(R.id.leftPlayer1Workstation).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer2Workstation).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer3Workstation).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer1Workstation).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer2Workstation).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer3Workstation).setVisibility(View.GONE);
        findViewById(R.id.topPlayerWorkstation).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer1Confirmed).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer2Confirmed).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer3Confirmed).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer1Confirmed).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer2Confirmed).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer3Confirmed).setVisibility(View.GONE);
        findViewById(R.id.topPlayerConfirmed).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer1TextView).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer2TextView).setVisibility(View.GONE);
        findViewById(R.id.leftPlayer3TextView).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer1TextView).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer2TextView).setVisibility(View.GONE);
        findViewById(R.id.rightPlayer3TextView).setVisibility(View.GONE);
        findViewById(R.id.topPlayerTextView).setVisibility(View.GONE);
    }
}

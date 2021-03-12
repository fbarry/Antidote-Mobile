package com.example.antidote_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class GameActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    public static final int millisPerUpdate = 4_000;
    Timer refreshTimer;
    Game game;
    Player currentPlayer;
    CardHandler ch;
    ArrayList<Player> players;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());

        players = new ArrayList<>();
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
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ch = findViewById(R.id.cardHandler);
        ch.setCards(currentPlayer.cards());

        ch.setValueChangeListener(() -> {
            if (ch.lifted != null && game.currentActionType() != ActionType.NONE
                    && game.currentActionType() != ActionType.SYRINGE) {
                // We have selected a card (possibly just selected it)
                findViewById(R.id.confirmButton).setVisibility(View.VISIBLE);
            } else {
                // We have not selected a card (possibly just deselected it)
                findViewById(R.id.confirmButton).setVisibility(View.GONE);
            }
            if(ch.lifted != null && ch.lifted.type != CardType.SYRINGE) {
                game.setCurrentAction(ActionType.NONE);
                updateTurnTextView();
            }
        });

        TextView gameCodeTextView = findViewById(R.id.gameCodeTextView);
        gameCodeTextView.append(" " + game.roomCode());

        TextView turnTextView = findViewById(R.id.turnTextView);
        turnTextView.append(" " + currentPlayer.username());

        switch (game.numPlayers()) {
            case 1:
            case 2:
                findViewById(R.id.player3workstation).setVisibility(View.GONE);
                findViewById(R.id.player3TextView).setVisibility(View.GONE);
            case 3:
                findViewById(R.id.player4workstation).setVisibility(View.GONE);
                findViewById(R.id.player4TextView).setVisibility(View.GONE);
            case 4:
                findViewById(R.id.player5workstation).setVisibility(View.GONE);
                findViewById(R.id.player5TextView).setVisibility(View.GONE);
            case 5:
                findViewById(R.id.player6workstation).setVisibility(View.GONE);
                findViewById(R.id.player6TextView).setVisibility(View.GONE);
            case 6:
                findViewById(R.id.player7workstation).setVisibility(View.GONE);
                findViewById(R.id.player7TextView).setVisibility(View.GONE);
        }
        switch (game.numPlayers()) {
            case 7:
                ((TextView) findViewById(R.id.player7TextView)).setText(players.get(6).username());
            case 6:
                ((TextView) findViewById(R.id.player6TextView)).setText(players.get(5).username());
            case 5:
                ((TextView) findViewById(R.id.player5TextView)).setText(players.get(4).username());
            case 4:
                ((TextView) findViewById(R.id.player4TextView)).setText(players.get(3).username());
            case 3:
                ((TextView) findViewById(R.id.player3TextView)).setText(players.get(2).username());
            case 2:
                ((TextView) findViewById(R.id.player2TextView)).setText(players.get(1).username());
            case 1:
                ((TextView) findViewById(R.id.player1TextView)).setText(players.get(0).username());
        }

        refreshTimer = new Timer();

        update();
//        refreshTimer.scheduleAtFixedRate(new TimerTask() {
//            public void run() {
//                runOnUiThread(() -> update());
//            }
//        }, 0, millisPerUpdate);

    }

    public void gameRefresh(View v) {
        update();
    }

    public void update() {
        // update our game
        // update our players arraylist
        ParseQuery.getQuery("Game").getInBackground(game.getObjectId(), (object, e) -> {
            game = (Game) object;

            ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
            getPlayers.whereContainedIn("objectId", game.players());
            System.out.println(game.players());

            try {
                List<ParseObject> parseObjects = getPlayers.find();
                System.out.println("Got " + parseObjects.size() + " updated players, previously had " + players.size());
                for (int i = 0; i < players.size(); i++) {
                    if (parseObjects.get(i).getObjectId().equals(currentPlayer.getObjectId())) {
                        // Update currentPlayer and the game's UI
                        ArrayList<String> oldCards = currentPlayer.cards();
                        currentPlayer = (Player) parseObjects.get(i);
                        if (!oldCards.equals(ch.getCardData())) {
                            ch.setCards(currentPlayer.cards());
                            System.out.println("Updated cards!");
                        }
                        if (currentPlayer.isLocked()) {
                            ch.forceAll();
                            ch.forceSelect(currentPlayer.selectedIdx());
                            Drawable nimg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_cancel_24, null);
                            ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
                        } else {
                            ch.deselect();
                            Drawable nimg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_check_circle_24, null);
                            ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
                        }
                        ch.selectable = !currentPlayer.isLocked();

                    }
                    // Update the players arraylist with this object
                    if (parseObjects.get(i).getObjectId().equals(players.get(i).getObjectId())) {
                        players.set(i, (Player) parseObjects.get(i));
                    }
                }
                // Show/hide buttons when necessary
                updateActionVisibilities();
                if (game.host().equals(currentPlayer.getObjectId())) {
                    // We're the host, perhaps we should complete the computation of a turn?
                    int numLocked = 0;
                    for (Player p : players) if (p.isLocked()) numLocked++;
                    System.out.println(numLocked + " players were locked, out of " + game.numPlayers());
                    if (numLocked == game.numPlayers()) {
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
                    } else if (numLocked == 2) {
                        switch (ActionType.fromString(game.currentAction())) {
                            case TRADE:
                            case NONE:
                            default:
                        }
                    }
                }
                for (Player p : players) {
                    System.out.println(p.getObjectId() + "," + p.username());
                }
                updateTurnTextView();
            } catch (ParseException ignored) {
            }
        });

    }

    void updateTurnTextView() {
        TextView turnTextView = findViewById(R.id.turnTextView);
        turnTextView.setText(R.string.turn_);
        turnTextView.append(" " + players.get(game.currentTurn()).username());
        turnTextView.append(", Action: ");
        turnTextView.append(game.currentAction());
    }

    void updateActionVisibilities() {
        if (game.players().get(game.currentTurn()).equals(currentPlayer.getObjectId())
                && game.currentActionType() == ActionType.NONE) {
            findViewById(R.id.passCardsLeftButton).setVisibility(View.VISIBLE);
            findViewById(R.id.syringeButton).setVisibility(currentPlayer.hasSyringe() ? View.VISIBLE : View.GONE);
            findViewById(R.id.discardCardsButton).setVisibility(View.VISIBLE);
            findViewById(R.id.passCardsRightButton).setVisibility(View.VISIBLE);

        } else {
            findViewById(R.id.passCardsLeftButton).setVisibility(View.GONE);
            findViewById(R.id.syringeButton).setVisibility(View.GONE);
            findViewById(R.id.discardCardsButton).setVisibility(View.GONE);
            findViewById(R.id.passCardsRightButton).setVisibility(View.GONE);
        }
    }

    void finalizeAction() {
        game.setCurrentTurn((game.currentTurn() + 1) % game.numPlayers());
        game.setCurrentAction(ActionType.NONE.getText());
        ParseObject.saveAllInBackground(players, e -> game.saveInBackground(e1 -> update()));
    }

    // 1 for left, -1 for right
    void performPass(int direction) {
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
            ArrayList<String> pCards = p.cards(), pWorkstation = p.workstation();
            pWorkstation.add(pCards.remove(p.selectedIdx()));
            p.setCards(pCards);
            p.setWorkstation(pWorkstation);
            p.deselect();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (game.host().equals(currentPlayer.getObjectId())) {
            game.deleteGame();
        } else {
            game.removePlayer(currentPlayer.getObjectId());
        }
    }

    public void openInfoPage(View v) {
        startActivity(new Intent(GameActivity.this, InfoPageActivity.class));
    }

    public void workstation1(View v) {
        viewWorkstation(0);
    }

    public void workstation2(View v) {
        viewWorkstation(1);
    }

    public void workstation3(View v) {
        viewWorkstation(2);
    }

    public void workstation4(View v) {
        viewWorkstation(3);
    }

    public void workstation5(View v) {
        viewWorkstation(4);
    }

    public void workstation6(View v) {
        viewWorkstation(5);
    }

    public void workstation7(View v) {
        viewWorkstation(6);
    }

    // playerNum is zero-indexed
    public void viewWorkstation(int playerNum) {
        Dialog myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.activity_workstation);
        myDialog.setCancelable(true);
        myDialog.setTitle("gaming");

        myDialog.show();

        CardHandler workstationCh = myDialog.findViewById(R.id.cardHandlerWorkstation);
        workstationCh.setCards(players.get(playerNum).workstation());

        TextView whoseTextView = myDialog.findViewById(R.id.whoseWorkstationTextView);
        whoseTextView.setText(players.get(playerNum).username());
        whoseTextView.append("'s Workstation");


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
        if (!ch.selectable) {
            // we meant to deselect
            ch.deselect();
            ch.selectable = true;
            Drawable nimg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_check_circle_24, null);
            ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
            currentPlayer.setIsLocked(false);
            currentPlayer.setSelectedIdx(-1);
            currentPlayer.setCards(ch.getCardData());
            currentPlayer.saveInBackground(e -> System.out.println("Saved deselect successfully!"));
        } else {
            System.out.println("Selected " + selectedIdx);
            currentPlayer.setIsLocked(true);
            currentPlayer.setCards(ch.getCardData());
            currentPlayer.setSelectedIdx(selectedIdx);
            ch.selectable = false;
            Drawable nimg = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_baseline_cancel_24, null);
            ((ImageButton) findViewById(R.id.confirmButton)).setImageDrawable(nimg);
            currentPlayer.saveInBackground(e -> System.out.println("Saved select successfully!"));
        }
    }

}

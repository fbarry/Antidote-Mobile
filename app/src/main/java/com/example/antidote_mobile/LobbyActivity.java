package com.example.antidote_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LobbyActivity extends AppCompatActivity implements ChatDialogActivity {

    @SuppressWarnings("unused")
    public static final int millisPerUpdate = 4000;
    Game game;
    Player currentPlayer;
    Timer refreshTimer;
    RecyclerView playerList;
    PlayerAdapter adapter;
    ImageButton chatButton;
    ChatDialog chatDialog;

    boolean gameScreenLaunched = false;

    ArrayDeque<Player> aiInQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        aiInQueue = new ArrayDeque<>();

        game = (Game) getIntent().getSerializableExtra("gameInfo");
        currentPlayer = (Player) getIntent().getSerializableExtra("currentPlayer");

        playerList = findViewById(R.id.playerList);

        LinearLayoutManager llm = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL,
                false);
        playerList.setLayoutManager(llm);

        adapter = new PlayerAdapter(LobbyActivity.this, game, currentPlayer.isHost());
        playerList.setAdapter(adapter);

        chatButton = findViewById(R.id.chatButtonLobby);

        chatDialog = new ChatDialog(LobbyActivity.this, game.getObjectId(), currentPlayer.username());
        chatDialog.create();

        if (!currentPlayer.isHost()) {
            Button startGameButton = findViewById(R.id.startGameButton);
            startGameButton.setVisibility(View.GONE);
            Button endGameButton = findViewById(R.id.endGameButton);
            endGameButton.setVisibility(View.GONE);
            Button AIButton = findViewById(R.id.lobbyAIButton);
            AIButton.setVisibility(View.GONE);
        } else {
            Button leaveGameButton = findViewById(R.id.leaveGameButton);
            leaveGameButton.setVisibility(View.GONE);
        }

        TextView roomCodeTextView = findViewById(R.id.roomCodeTextView);
        roomCodeTextView.setText(game.roomCode());

        updatePlayerList();

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (currentPlayer != null && game != null && currentPlayer.isHost())
            game.deleteGame();
        else if (currentPlayer != null && game != null)
            game.removePlayer(currentPlayer.getObjectId());
    }

    public void update() {
        if (game == null) return;

        updateGame();
        updateChat();
    }

    public void updateGame() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Game");
        query.getInBackground(game.getObjectId(), (object, e) -> {
            if (e != null) {
                if (object == null) {
                    game = null;
                    LobbyActivity.this.finish();
                }
            } else {
                game = (Game) object;
                adapter.setGame(game);
                updatePrivacyVisuals();
                updatePlayerList();
                updateGameScreen();
            }
        });
    }

    public void updatePrivacyVisuals() {
        String pub = getResources().getString(R.string.public_);
        String priv = getResources().getString(R.string.private_);

        if (game.host().equals(currentPlayer.getObjectId())) {
            findViewById(R.id.publicTextView).setVisibility(View.GONE);
            findViewById(R.id.lobbyPrivateButton).setVisibility(View.VISIBLE);
            ((Button) findViewById(R.id.lobbyPrivateButton)).setText(game.isPrivate() ? priv : pub);
        } else {
            findViewById(R.id.publicTextView).setVisibility(View.VISIBLE);
            findViewById(R.id.lobbyPrivateButton).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.publicTextView)).setText(game.isPrivate() ? priv : pub);
        }
    }

    public void privateButton(View v) {
        game.setPrivate(!game.isPrivate());
        game.saveInBackground(e -> update());
    }

    public void updateChat() {
        chatDialog.refreshMessages();
    }

    public void showChatNotification() {
        if (!chatDialog.isShowing()) {
            chatButton.setImageResource(R.drawable.ic_baseline_mark_chat_unread_24);
        }
    }

    public void updateGameScreen() {
        if (game.numCards() > 0 || currentPlayer != null && currentPlayer.needsGameOverScreen()) {
            ParseQuery<ParseObject> query = new ParseQuery<>("Player");

            try {
                currentPlayer = (Player) query.get(currentPlayer.getObjectId());
                goToGameScreen();
            } catch (ParseException e) {
                Utilities.showInformationAlert(this,
                        R.string.could_not_connect,
                        R.string.check_your_internet,
                        (dialog, which) -> {
                            Button leave = findViewById(R.id.leaveGameButton);
                            leave.callOnClick();
                        });
                e.printStackTrace();
            }
        }
    }

    public void updatePlayerList() {
        ParseQuery<ParseObject> getPlayers = new ParseQuery<>("Player");
        getPlayers.whereContainedIn("objectId", game.players());

        getPlayers.findInBackground((objects, e) -> {
            boolean stillInTheGame = false;

            ArrayList<Player> list = new ArrayList<>();
            for (ParseObject po : objects) {
                System.out.println("Found player with objectid " + po.getObjectId());
                list.add((Player) po);
                if (po.getObjectId().equals(currentPlayer.getObjectId())) {
                    stillInTheGame = true;
                }
            }

            if (!stillInTheGame) {
                Utilities.showInformationAlert(this,
                        R.string.you_were_kicked,
                        R.string.sorry,
                        (dialog, which) -> {
                            currentPlayer = null;
                            LobbyActivity.this.finish();
                        });
            }

            adapter.setPlayers(list);
            adapter.notifyDataSetChanged();

            if (!aiInQueue.isEmpty()) {
                while (!aiInQueue.isEmpty()) {
                    game.addPlayer(Objects.requireNonNull(aiInQueue.poll()));
                }

                game.saveInBackground(e1 -> update());
            }
        });
    }

    public void AIButton(View v) {
        if (game.numPlayers() + aiInQueue.size() >= 7) return;
        aiInQueue.add(PlayerAI.createPlayerAI());
    }

    public void leaveGame(View v) {
        game.removePlayer(currentPlayer.getObjectId());
        currentPlayer = null;
        LobbyActivity.this.finish();
    }

    public void endGame(View v) {
        Utilities.showConfirmationAlert(this,
                "Are you sure you want to end this game?",
                "You cannot undo this action.",
                (dialog, which) -> {
                    game.deleteGame();
                    currentPlayer = null;
                    game = null;
                    LobbyActivity.this.finish();
                });
    }

    public void startGame(View v) {
        if (game.numPlayers() < 3) return;

        game.initialize(currentPlayer);

        try {
            game.save();
        } catch (ParseException e) {
            Utilities.showTwoPromptAlert(this,
                    R.string.could_not_connect,
                    R.string.check_your_internet,
                    R.string.try_again,
                    R.string.cancel,
                    (dialog, which) -> {
                        startGame(v);
                        dialog.dismiss();
                    }, (dialog, which) -> dialog.dismiss());
        }
    }

    public void goToGameScreen() {
        if (gameScreenLaunched) return;

        gameScreenLaunched = true;

        Intent goToGame = new Intent(LobbyActivity.this, GameActivity.class);
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        sendGame.putSerializable("currentPlayer", currentPlayer);
        goToGame.putExtras(sendGame);
        startActivity(goToGame);

        LobbyActivity.this.finish();
    }

    public void onClickChat(View v) {
        launchChatPopup();
        chatButton.setImageResource(R.drawable.ic_baseline_chat_bubble_24);
    }

    public void launchChatPopup() {
        chatDialog.show();
    }
}

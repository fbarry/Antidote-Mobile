import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.Game;
import com.example.antidote_mobile.LobbyActivity;
import com.example.antidote_mobile.MainActivity;
import com.example.antidote_mobile.Player;
import com.example.antidote_mobile.R;
import com.example.antidote_mobile.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static java.util.regex.Pattern.matches;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class LobbyTest {

    @Rule
    public ActivityTestRule<LobbyActivity> lobbyActivityTestRule
            = new ActivityTestRule<>(LobbyActivity.class, true, false);

    @Before
    public void initIntents() { Intents.init(); }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testLobbyDisplaysCorrectNumberOfPlayers() {
        Activity lobby = getNewLobby();

        Game game = (Game) (lobby.getIntent().getSerializableExtra("gameInfo"));

        TextView playerListTextView = lobby.findViewById(R.id.playerList);
        String listInText = playerListTextView.getText().toString();
        String[] list = listInText.split("\n");

        assertEquals(game.numPlayers, list.length);
    }

    @Test
    public void testLobbyDisplaysCorrectGameCode() {
        Activity lobby = getNewLobby();

        Game game = (Game) (lobby.getIntent().getSerializableExtra("gameInfo"));

        TextView roomCodeTextView = lobby.findViewById(R.id.roomCodeTextView);
        assertEquals(game.roomCode, roomCodeTextView.getText().toString());
    }

    @Test
    public void testLobbyHasGameIntentWhenLaunching() {
        assertNotNull(getNewLobby().getIntent().getSerializableExtra("gameInfo"));
    }

    private Activity getNewLobby() {
        Intent goToLobby = new Intent();
        Game game = Game.createGame(new Player().createPlayer(User.getNewGuest()));
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        goToLobby.putExtras(sendGame);

        return lobbyActivityTestRule.launchActivity(goToLobby);
    }
}
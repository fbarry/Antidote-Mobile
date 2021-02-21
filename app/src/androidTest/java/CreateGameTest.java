import android.app.Activity;
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

import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CreateGameTest {

    @Rule
    public ActivityTestRule<LobbyActivity> lobbyActivityTestRule
            = new ActivityTestRule<>(LobbyActivity.class, true, false);

    @Rule
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class, true, true);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testClickCreateGameGoesToLobby() {
        Espresso.onView(ViewMatchers.withId(R.id.createGameButton)).perform(click());

        Game game = Game.createGame(new Player().createPlayer(User.getNewGuest()));

        Intent goToLobby = new Intent();
        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        goToLobby.putExtras(sendGame);

        Activity lobby = lobbyActivityTestRule.launchActivity(goToLobby);
        assertNotNull(lobby);
    }

    @Test
    public void testCreateNewGameFunction() {
        Player player = new Player().createPlayer(User.getNewGuest());
        Game game = Game.createGame(player);
        assertNotNull(game);
    }
}

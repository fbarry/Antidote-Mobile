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
import com.example.antidote_mobile.InfoPageActivity;
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
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class JoinGameTest {

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
    public void testClickOnlyWithNonemptyTextBox() {
        TextView joinCodeTextView = mainActivityTestRule.getActivity().findViewById(R.id.joinCodeTextView);
        joinCodeTextView.setText("");

        Espresso.onView(ViewMatchers.withId(R.id.joinGameButton)).perform(click());
        intended(hasComponent(MainActivity.class.getName()));

        Game game = Game.createGame(new Player().createPlayer(User.getNewGuest()));
        joinCodeTextView.setText(game.roomCode);
        intended(hasComponent(LobbyActivity.class.getName()));
    }

    @Test
    public void testClickJoinGameGoesToLobby() {
        Espresso.onView(ViewMatchers.withId(R.id.createGameButton)).perform(click());
        intended(hasComponent(LobbyActivity.class.getName()));
    }
}

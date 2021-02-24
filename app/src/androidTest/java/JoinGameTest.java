import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.test.espresso.DataInteraction;
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
import com.parse.ParseException;
import com.parse.ParseUser;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
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
    public ActivityTestRule<MainActivity> mainActivityTestRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testClickJoinGameGoesToLobbyWithGameObject() {
        User user = User.signUpGuest();

        if (user == null) return;

        Player player = new Player().createPlayer(user);
        Game game = Game.createGame(player);
        assert game != null;
        Espresso.onView(ViewMatchers.withId(R.id.joinCodeTextView))
                .perform(replaceText(game.roomCode));
        Espresso.onView(ViewMatchers.withId(R.id.joinGameButton)).perform(click());
        intended(hasComponent(LobbyActivity.class.getName()));

        try {
            ParseUser.deleteAll(Collections.singletonList(user));
            // Can be added for cleanup when Player extends ParseObject
            // ParseObject.deleteAll(Arrays.asList(player));
            game.deleteGame();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}

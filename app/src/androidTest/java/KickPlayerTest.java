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

import com.example.antidote_mobile.AntidoteMobile;
import com.example.antidote_mobile.Game;
import com.example.antidote_mobile.LobbyActivity;
import com.example.antidote_mobile.MainActivity;
import com.example.antidote_mobile.Player;
import com.example.antidote_mobile.R;
import com.example.antidote_mobile.User;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class KickPlayerTest {

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testConfirmationWhenKick() {
        User user = User.signIn("randomUser", "randomPassword");
        assert user != null;

        Player player = Player.createPlayer(user, true);
        assert player != null;

        Game game = Game.createGame(player);
        assert game != null;

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("gameInfo", game);
        bundle.putSerializable("currentPlayer", player);
        intent.putExtras(bundle);

        ActivityTestRule<LobbyActivity> lobbyActivityTestRule =
                new ActivityTestRule<>(LobbyActivity.class, true, false);
        lobbyActivityTestRule.launchActivity(intent);

        LobbyActivity lobby = lobbyActivityTestRule.getActivity();
        // Figure out how to click a button on a recycler view to test kicking

        game.deleteGame();
    }
}

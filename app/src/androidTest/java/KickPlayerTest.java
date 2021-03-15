import android.content.Intent;
import android.os.Bundle;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.Game;
import com.example.antidote_mobile.LobbyActivity;
import com.example.antidote_mobile.Player;
import com.example.antidote_mobile.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.ChatDialog;
import com.example.antidote_mobile.Game;
import com.example.antidote_mobile.LobbyActivity;
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

@RunWith(AndroidJUnit4.class)
public class ChatTest {

    @Rule
    public ActivityTestRule<LobbyActivity> lobbyActivityTestRule =
            new ActivityTestRule<>(LobbyActivity.class, true, false);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testButtonOpensChat() {
        Game game = getNewLobby();
        Espresso.onView(ViewMatchers.withId(R.id.chatButtonLobby)).perform(click());

        assert(!lobbyActivityTestRule.getActivity().hasWindowFocus());

        game.deleteGame();
    }

    @Test
    public void testImageWhenOpenChatHasNoNotification() {
        Game game = getNewLobby();

        ImageButton chatButton = lobbyActivityTestRule.getActivity().findViewById(R.id.chatButtonLobby);
        Drawable pastPicture = chatButton.getDrawable();

        Espresso.onView(ViewMatchers.withId(R.id.chatButtonLobby)).perform(click());

        Drawable currPicture = chatButton.getDrawable();

        assert(!pastPicture.equals(currPicture));

        game.deleteGame();
    }

    private Game getNewLobby() {
        Intent goToLobby = new Intent();
        User user = User.signIn("randomUser", "randomPassword");

        assert user != null;

        Player player = Player.createPlayer(user, true);

        assert player != null;

        Game game = Game.createGame(player);

        assert game != null;

        Bundle sendGame = new Bundle();
        sendGame.putSerializable("gameInfo", game);
        sendGame.putSerializable("currentPlayer", player);
        goToLobby.putExtras(sendGame);

        lobbyActivityTestRule.launchActivity(goToLobby);

        return game;
    }
}

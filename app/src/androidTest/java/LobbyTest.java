import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.Game;
import com.example.antidote_mobile.LobbyActivity;
import com.example.antidote_mobile.Player;
import com.example.antidote_mobile.R;
import com.example.antidote_mobile.User;
import com.parse.ParseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

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
    public void testLobbyDisplaysCorrectGameCode() {
        Activity lobby = getNewLobby();

        Game game = (Game) (lobby.getIntent().getSerializableExtra("gameInfo"));

        TextView roomCodeTextView = lobby.findViewById(R.id.roomCodeTextView);
        assertEquals(game.roomCode(), roomCodeTextView.getText().toString());

        game.deleteGame();
        ParseUser.logOutInBackground();
    }

    private Activity getNewLobby() {
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

        return lobbyActivityTestRule.launchActivity(goToLobby);
    }
}

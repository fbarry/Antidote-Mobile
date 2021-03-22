import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.AntidoteMobile;
import com.example.antidote_mobile.ProfilePageActivity;
import com.example.antidote_mobile.R;
import com.example.antidote_mobile.User;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class FriendTest {

    @Rule
    public ActivityTestRule<ProfilePageActivity> profileActivityTestRule =
            new ActivityTestRule<>(ProfilePageActivity.class, true, false);

    @Before
    public void initIntents() {
        Intents.init();
    }

    @After
    public void releaseIntents() {
        Intents.release();
    }

    @Test
    public void testSaysUnfriendWhenFriends() {
        getNewProfilePage(true, false);
        ProfilePageActivity activity = profileActivityTestRule.getActivity();
        Button friend = activity.findViewById(R.id.friendButton);
        assertEquals("Unfriend", friend.getText().toString());
        cleanup();
    }

    @Test
    public void testSaysFriendWhenNotFriends() {
        getNewProfilePage(false, false);
        ProfilePageActivity activity = profileActivityTestRule.getActivity();
        Button friend = activity.findViewById(R.id.friendButton);
        assertEquals("Friend", friend.getText().toString());
        cleanup();
    }

    @Test
    public void testFriendsListShowsFriends() {
        getNewProfilePage(true, true);
        ProfilePageActivity activity = profileActivityTestRule.getActivity();
        RecyclerView rv = activity.findViewById(R.id.friendsList);
        assertEquals(Objects.requireNonNull(rv.getAdapter()).getItemCount(), user.getFriends().size());
        cleanup();
    }

    User user;
    String fakeFriend = "LQqVSgsBQC";

    private void getNewProfilePage(boolean addFriend, boolean ofYourself) {
        user = User.signIn("randomUser", "randomPassword");
        AntidoteMobile.currentUser = user;
        assert user != null;

        if (addFriend) user.addFriend(fakeFriend);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", fakeFriend);

        List<ParseUser> res = null;

        try {
            res = query.find();
        } catch (ParseException e) {
            assert false;
        }

        assert res != null;
        assert res.size() != 0;

        User user2 = (User) res.get(0);

        Bundle bundle = new Bundle();
        if (!ofYourself) bundle.putSerializable("user", user2);
        Intent intent = new Intent();
        intent.putExtras(bundle);

        profileActivityTestRule.launchActivity(intent);
    }

    private void cleanup() {
        user.removeFriend(fakeFriend);
        ParseUser.logOutInBackground();
        AntidoteMobile.currentUser = null;
    }
}

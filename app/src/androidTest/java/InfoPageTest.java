import android.view.View;
import android.widget.ScrollView;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.example.antidote_mobile.InfoPageActivity;
import com.example.antidote_mobile.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InfoPageTest {

    @Rule
    public ActivityTestRule<InfoPageActivity> activityRule
            = new ActivityTestRule<>(InfoPageActivity.class, true, true);

    @Before
    public void startIntents() {
        Intents.init();
    }

    @After
    public void relaseIntents() {
        Intents.release();
    }

    @Test
    // Test if the back button closes the InfoPageActivity
    public void testBackButtonClosesInfoPage() {
        Espresso.onView(ViewMatchers.withId(R.id.backButton)).perform(click());
        assertTrue(activityRule.getActivity().isFinishing());
    }

    @Test
    public void testScrollViewStartsAtZero() {
        int scroll_y = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();
        assertEquals(0, scroll_y);
    }

    @Test
    public void testClickSetup() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_setup)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.header_setup).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }

    @Test
    public void testClickObjective() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_objective)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.header_objective).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }

    @Test
    public void testClickGameplay() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_gameplay)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.header_gameplay).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }

    @Test
    public void testClickGameplayDiscard() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_gameplay)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.gameplay_text2)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.subheader_discard).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }

    @Test
    public void testClickGameplayTrade() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_gameplay)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.gameplay_text3)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.subheader_trade).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }

    @Test
    public void testClickGameplaySyringe() {
        Espresso.onView(ViewMatchers.withId(R.id.toc_gameplay)).perform(click());
        Espresso.onView(ViewMatchers.withId(R.id.gameplay_text4)).perform(click());

        int exp = activityRule.getActivity().findViewById(R.id.subheader_syringe).getTop();
        int real = activityRule.getActivity().findViewById(R.id.infoScroll).getScrollY();

        ScrollView scroll = (activityRule.getActivity().findViewById(R.id.infoScroll));
        scroll.fullScroll(View.FOCUS_DOWN);
        exp = Math.min(exp, scroll.getScrollY());

        assertEquals(exp, real);
    }


}
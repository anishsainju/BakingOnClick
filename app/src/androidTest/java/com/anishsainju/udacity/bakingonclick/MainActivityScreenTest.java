package com.anishsainju.udacity.bakingonclick;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4.class)
public class MainActivityScreenTest {

    public static final int RECIPE_POS = 0;
    public static final String RECIPE_NAME = "Nutella Pie Serves: 8";
    public static final int RECIPE_STEP_COUNT = 8;

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickRecyclerViewItem_OpensRecipeListActivity() {
        // Uses {@link Espresso#onData(org.hamcrest.Matcher)} to get a reference to a specific
        // recyclerview item and clicks it.
        onView(withId(R.id.rv_recipe_cards)).perform(RecyclerViewActions.actionOnItemAtPosition(RECIPE_POS, click()));
        // Checks that the RecipeListActivity opens with the correct recipe name displayed
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(RECIPE_NAME)));
    }

    @Test
    public void clickRecipe_CheckItemCount() {
        onView(withId(R.id.rv_recipe_cards)).perform(RecyclerViewActions.actionOnItemAtPosition(RECIPE_POS, click()));
        onView(withId(R.id.recipe_list)).check(new RecyclerViewItemCountAssertion(RECIPE_STEP_COUNT));
    }
}

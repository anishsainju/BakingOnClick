package com.anishsainju.udacity.bakingonclick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.anishsainju.udacity.bakingonclick.model.IngreStep;

import java.util.ArrayList;

import static com.anishsainju.udacity.bakingonclick.RecipeDetailFragment.ARG_INGRESTEPLIST;
import static com.anishsainju.udacity.bakingonclick.RecipeDetailFragment.ARG_INGRESTEPPOS;

/**
 * An activity representing a single Recipe detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeListActivity}.
 */
public class RecipeDetailActivity extends AppCompatActivity {

    private Button btn_previous;
    private Button btn_next;

    private ArrayList<IngreStep> ingreStepArrayList;
    private int ingreStepPos;
    private IngreStep ingreStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);
        btn_previous = findViewById(R.id.btn_previous);
        btn_next = findViewById(R.id.btn_next);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        Bundle bundle = getIntent().getBundleExtra(RecipeDetailFragment.ARG_INGRESTEPBUNDLE);
        ingreStepArrayList = bundle.getParcelableArrayList(ARG_INGRESTEPLIST);
        if (savedInstanceState == null) {
            ingreStepPos = bundle.getInt(ARG_INGRESTEPPOS);

            handlePrevNextBtnVisibility(ingreStepPos);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, fragment)
                    .commit();
        } else {
            ingreStepPos = savedInstanceState.getInt(RecipeDetailFragment.ARG_INGRESTEPPOS);
        }

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(RecipeDetailFragment.ARG_INGRESTEPLIST, ingreStepArrayList);
                ingreStepPos = ingreStepPos - 1;
                arguments.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, ingreStepPos);
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.recipe_detail_container, fragment);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                handlePrevNextBtnVisibility(ingreStepPos);
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(RecipeDetailFragment.ARG_INGRESTEPLIST, ingreStepArrayList);
                ingreStepPos = ingreStepPos + 1;
                arguments.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, ingreStepPos);
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
                // Begin the transaction
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                // Replace the contents of the container with the new fragment
                ft.replace(R.id.recipe_detail_container, fragment);
                // or ft.add(R.id.your_placeholder, new FooFragment());
                // Complete the changes added above
                ft.commit();
                handlePrevNextBtnVisibility(ingreStepPos);
            }
        });
    }

    private void handlePrevNextBtnVisibility(int ingreStepPos) {
        if (ingreStepPos == 0) {
            btn_previous.setVisibility(View.INVISIBLE);
        } else if (ingreStepPos == ingreStepArrayList.size() - 1) {
            btn_next.setVisibility(View.INVISIBLE);
        } else {
            btn_previous.setVisibility(View.VISIBLE);
            btn_next.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, ingreStepPos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, RecipeListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

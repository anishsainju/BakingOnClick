package com.anishsainju.udacity.bakingonclick;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.anishsainju.udacity.bakingonclick.model.IngreStep;
import com.anishsainju.udacity.bakingonclick.model.Recipe;
import com.anishsainju.udacity.bakingonclick.model.Step;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeListActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String RECIPE = "selected_recipe";
    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private int selectedIngreStepPos;

    private Recipe recipe;
    private ArrayList<IngreStep> ingreStepList;
    private MultiViewItemTypeViewAdapter multiViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);
//        setTitle(getString(R.string.AppName));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }

        // Obtain the Parcelable Movie object
        recipe = intent.getParcelableExtra(RECIPE);
        if (recipe == null) {
            closeOnError();
            return;
        }

        setTitle(recipe.getName() + " Serves: " + String.valueOf(recipe.getServings()));

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        if (findViewById(R.id.recipe_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        View recyclerView = findViewById(R.id.recipe_list);
        assert recyclerView != null;
        this.ingreStepList = getIngreStepList();
        setupRecyclerView((RecyclerView) recyclerView, ingreStepList);

        if (savedInstanceState == null) {
            selectedIngreStepPos = 0;
            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelableArrayList(RecipeDetailFragment.ARG_INGRESTEPLIST, ingreStepList);
                arguments.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, selectedIngreStepPos);
                multiViewAdapter.setSelectedPos(selectedIngreStepPos);
                RecipeDetailFragment fragment = new RecipeDetailFragment();
                fragment.setArguments(arguments);
//            mParentActivity.
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipe_detail_container, fragment)
                        .commit();
            }
        } else {
            selectedIngreStepPos = savedInstanceState.getInt(RecipeDetailFragment.ARG_INGRESTEPPOS);
            multiViewAdapter.setSelectedPos(selectedIngreStepPos);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, selectedIngreStepPos);
        super.onSaveInstanceState(outState);
    }

    private ArrayList<IngreStep> getIngreStepList() {
        ArrayList<IngreStep> ingreStepList = new ArrayList<>();
        IngreStep ingreStep = new IngreStep(IngreStep.VIEW_TYPE_INGREDIENT, recipe.getIngredients());
        ingreStepList.add(ingreStep);
        for (Step step : recipe.getSteps()) {
            IngreStep ingreStep1 = new IngreStep(IngreStep.VIEW_TYPE_STEP, step.getId(), step.getShortDescription(), step.getDescription(), step.getVideoURL(), step.getThumbnailURL());
            ingreStepList.add(ingreStep1);
        }
        return ingreStepList;
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.detail_error_message, Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<IngreStep> ingreStepList) {
        multiViewAdapter = new MultiViewItemTypeViewAdapter(this, ingreStepList, getApplicationContext(), mTwoPane, this);
        recyclerView.setAdapter(multiViewAdapter);
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

    @Override
    public void onClick(View view) {
        selectedIngreStepPos = (int) view.getTag();
        Bundle arguments = new Bundle();
        arguments.putParcelableArrayList(RecipeDetailFragment.ARG_INGRESTEPLIST, ingreStepList);
        arguments.putInt(RecipeDetailFragment.ARG_INGRESTEPPOS, selectedIngreStepPos);
        if (mTwoPane) {
            multiViewAdapter.setSelectedPos(selectedIngreStepPos);
            RecipeDetailFragment fragment = new RecipeDetailFragment();
            fragment.setArguments(arguments);
//            mParentActivity.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipe_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, RecipeDetailActivity.class);
//            intent.putExtra(RecipeDetailFragment.ARG_INGRESTEPLIST, ingreStepList);
//            intent.putExtra(RecipeDetailFragment.ARG_INGRESTEPPOS, selectedIngreStepPos);
            intent.putExtra(RecipeDetailFragment.ARG_INGRESTEPBUNDLE, arguments);
            context.startActivity(intent);
        }
    }
/*
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int VIEW_TYPE_INGREDIENT = 0;
        private static final int VIEW_TYPE_STEP = 1;

        private final RecipeListActivity mParentActivity;
        private final List<Object> mValues;
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                Step selectedStep = (Step) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
//                    arguments.putString(RecipeDetailFragment.ARG_ITEM_ID, item.id);
                    arguments.putParcelable(RecipeDetailFragment.ARG_STEP, selectedStep);
                    RecipeDetailFragment fragment = new RecipeDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.recipe_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, RecipeDetailActivity.class);
//                    intent.putExtra(RecipeDetailFragment.ARG_ITEM_ID, item.id);
                    intent.putExtra(RecipeDetailFragment.ARG_STEP, selectedStep);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(RecipeListActivity parent,
                                      List<Object> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public int getItemViewType(int position) {
            if (mValues.get(position) instanceof Ingredient) {
                return VIEW_TYPE_INGREDIENT;
            } else if (mValues.get(position) instanceof Step) {
                return VIEW_TYPE_STEP;
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder viewHolder;
            switch (viewType) {
                case VIEW_TYPE_INGREDIENT:
                    View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_content, parent, false);
                    viewHolder = new IngredientViewHolder(view1);
                    break;
                case VIEW_TYPE_STEP:
                    View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_content, parent, false);
                    viewHolder = new StepViewHolder(view2);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewType);
            }
            return viewHolder;
//            View view = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.recipe_list_content, parent, false);
//            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int pos) {
            switch (viewHolder.getItemViewType()) {
                case VIEW_TYPE_INGREDIENT:
                    IngredientViewHolder ivh =(IngredientViewHolder) viewHolder;
                    Ingredient ingredient = (Ingredient) mValues.get(pos);
                    if (ingredient != null) {
                        ivh.ingredientTextview.setText(ingredient.getIngredient());
                    }
                    break;
                case VIEW_TYPE_STEP:
                    StepViewHolder svh = (StepViewHolder) viewHolder;
                    Step step = (Step) mValues.get(pos);
                    if (step != null) {
                        svh.shortDescTextview.setText(step.getShortDescription());
                        //TODO add other textviews
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid view type, value of " + viewHolder.getItemViewType());
            }
        }

        public class IngredientViewHolder extends RecyclerView.ViewHolder {
            TextView ingredientTextview;
            public IngredientViewHolder(@NonNull View itemView) {
                super(itemView);
                ingredientTextview = itemView.findViewById(R.id.tv_ingredients);
            }
        }

        public class StepViewHolder extends RecyclerView.ViewHolder {
            TextView shortDescTextview, descriptionTextview, videoUrlTextview, thumbnailUrlTextview;
            public StepViewHolder(@NonNull View itemView) {
                super(itemView);
                shortDescTextview = itemView.findViewById(R.id.);
                descriptionTextview = itemView.findViewById(R.id.);
                videoUrlTextview = itemView.findViewById(R.id.);
                thumbnailUrlTextview = itemView.findViewById(R.id.);
            }
        }
//        @Override
//        public void onBindViewHolder(final ViewHolder holder, int position) {
//            holder.mIdView.setText(String.valueOf(mValues.get(position).getId()));
//            holder.mContentView.setText(mValues.get(position).getShortDescription());
//
//            holder.itemView.setTag(mValues.get(position));
//            holder.itemView.setOnClickListener(mOnClickListener);
//        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

    }*/
}

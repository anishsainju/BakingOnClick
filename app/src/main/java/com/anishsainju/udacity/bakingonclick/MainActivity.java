package com.anishsainju.udacity.bakingonclick;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.Recipe;
import com.anishsainju.udacity.bakingonclick.utils.GetDataService;
import com.anishsainju.udacity.bakingonclick.utils.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements RecipeCardAdapter.RecipeCardAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    // UI components
    private RecyclerView mRecyclerViewRecipeCards;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;

    // Adapter for RecyclerView
    private RecipeCardAdapter mRecipeCardAdapter;

    private List<Recipe> recipeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setTitle(getString(R.string.AppName));

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        toolbar.setTitle(getTitle());

        /*
         * Using findViewById, we get a reference to our RecyclerView from xml. This allows us to
         * do things like set the adapter of the RecyclerView and toggle the visibility.
         */
        mRecyclerViewRecipeCards = findViewById(R.id.rv_recipe_cards);

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = findViewById(R.id.tv_error_message_display);

        /*
         * We will use GridLayoutManager to view the movie images in grid style in the recyclerView
         * numberOfColumns is set to 2 for Portrait orientation, and 3 for Landscape
         */
        int numberOfColumns = 1; //getColumnsNumByOrientation();
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns);
        mRecyclerViewRecipeCards.setLayoutManager(layoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
        mRecyclerViewRecipeCards.setHasFixedSize(true);

        /*
         * The MovieAdapter is responsible for linking our movies data with the Views that
         * will end up displaying our movie data.
         * Passing itself as clickHandler as this MainActivity implements
         * MovieAdapter.MovieAdapterOnClickHandler
         */
        mRecipeCardAdapter = new RecipeCardAdapter(this, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerViewRecipeCards.setAdapter(mRecipeCardAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         *
         * Please note: This so called "ProgressBar" isn't a bar by default. It is more of a
         * circle. We didn't make the rules (or the names of Views), we just follow them.
         */
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

//        loadRecepiJson(this, "baking.json");
        mLoadingIndicator.setVisibility(View.VISIBLE);

        GetDataService getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Recipe>> call = getDataService.getAllRecipes();
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.INVISIBLE);
                    processDataResponse(response.body());
                } else {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    mRecyclerViewRecipeCards.setVisibility(View.INVISIBLE);
                    mErrorMessageDisplay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                mRecyclerViewRecipeCards.setVisibility(View.INVISIBLE);
                mErrorMessageDisplay.setVisibility(View.VISIBLE);
            }
        });

    }

    private void processDataResponse(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        showRecipes(recipeList);
    }

    /**
     * Returns 3 for Landscape Orientation.
     * Returns 2 for otherwise (Portrait Orientation)
     *
     * @return int
     */
    private int getColumnsNumByOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 3;
        } else //if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            return 2;
    }


    private void showErrorMessage() {
        mRecyclerViewRecipeCards.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showRecipesDataView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mRecyclerViewRecipeCards.setVisibility(View.VISIBLE);
    }

    /*
        private void loadRecepiJson(Context context, String assetFileName) {
            parseJsonAndShow(JsonUtils.loadJSONFromAsset(context, assetFileName));
        }
        private void parseJsonAndShow(String jsonMoviesResponse) {
            try {
                recipeList = JsonUtils.parseRecipeJson(jsonMoviesResponse);
                showRecipes(recipeList);
            } catch (JSONException e) {
                e.printStackTrace();
                showErrorMessage();
            }
        }*/
    private void showRecipes(List<Recipe> recipesToShow) {
        if (recipesToShow.isEmpty()) {
            mErrorMessageDisplay.setText(R.string.msg_no_recipes);
            mErrorMessageDisplay.setVisibility(View.VISIBLE);
        } else {
            showRecipesDataView();
        }
        mRecipeCardAdapter.setRecipeData(recipesToShow);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(int position) {
        Intent startRecipeActivity = new Intent(this, RecipeListActivity.class);
        Recipe selectedRecipe = recipeList.get(position);
        startRecipeActivity.putExtra(RecipeListActivity.RECIPE, selectedRecipe);
        startActivity(startRecipeActivity);
    }

}

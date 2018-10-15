package com.anishsainju.udacity.bakingonclick;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.IngreStep;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

/**
 * A fragment representing a single Recipe detail screen.
 * This fragment is either contained in a {@link RecipeListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailActivity}
 * on handsets.
 */
public class RecipeDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
//    public static final String ARG_ITEM_ID = "item_id";
    //public static final String ARG_INGRESTEP = "ingredient_step";
    public static final String ARG_INGRESTEPLIST = "ingredient_step_list";
    public static final String ARG_INGRESTEPPOS = "ingredient_step_pos";
    public static final String ARG_INGRESTEPBUNDLE = "ingredient_step_bundle";

    /**
     * The dummy content this fragment is presenting.
     */
//    private DummyContent.DummyItem mItem;
    private ArrayList<IngreStep> ingreStepArrayList;
    private int ingreStepPos;
    private IngreStep ingreStep;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARG_INGRESTEPLIST) && bundle.containsKey(ARG_INGRESTEPPOS)) {
            ingreStepArrayList = bundle.getParcelableArrayList(ARG_INGRESTEPLIST);
            ingreStepPos = bundle.getInt(ARG_INGRESTEPPOS);
            ingreStep = ingreStepArrayList.get(ingreStepPos);
            Activity activity = this.getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(ARG_INGRESTEPLIST) && bundle.containsKey(ARG_INGRESTEPPOS)) {
            ingreStepArrayList = bundle.getParcelableArrayList(ARG_INGRESTEPLIST);
            ingreStepPos = bundle.getInt(ARG_INGRESTEPPOS);
            ingreStep = ingreStepArrayList.get(ingreStepPos);
            if (ingreStep != null) {
                switch (ingreStep.getViewType()) {
                    case IngreStep.VIEW_TYPE_INGREDIENT:
                        rootView = inflater.inflate(R.layout.recipe_detail_ingredients, container, false);
                        ((TextView) rootView.findViewById(R.id.ingredient_detail)).setText(R.string.things_you_need);
                        ListView listView = rootView.findViewById(R.id.listview_ingredients);
                        IngredientArrayAdaper ingredientArrayAdaper = new IngredientArrayAdaper(getContext(), ingreStep.getIngredientList());
                        listView.setAdapter(ingredientArrayAdaper);
                        break;
                    case IngreStep.VIEW_TYPE_STEP:
                        rootView = inflater.inflate(R.layout.recipe_detail_steps, container, false);
                        // Initialize the player view.
                        mPlayerView = rootView.findViewById(R.id.playerView);

                        // Initialize the player.
                        initializePlayer(Uri.parse(ingreStep.getVideoURL()));

                        // Load the question mark as the background image until the user answers the question.
                        mPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                                (getResources(), R.drawable.question_mark));
                        ((TextView) rootView.findViewById(R.id.step_shortDescription)).setText(ingreStep.getShortDescription());
                        TextView step_desc = rootView.findViewById(R.id.step_description);
                        step_desc.setText(ingreStep.getDescription());
                        step_desc.setMovementMethod(new ScrollingMovementMethod());
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid view type, value of " + ingreStep.getViewType());
                }

            }
        }
        return rootView;
    }

    /**
     * Initialize ExoPlayer.
     *
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this.getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this.getContext(), getString(R.string.BakingOnClick));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this.getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
    }
}

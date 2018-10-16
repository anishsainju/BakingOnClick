package com.anishsainju.udacity.bakingonclick;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.anishsainju.udacity.bakingonclick.model.IngreStep;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
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
public class RecipeDetailFragment extends Fragment implements ExoPlayer.EventListener {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_INGRESTEPLIST = "ingredient_step_list";
    public static final String ARG_INGRESTEPPOS = "ingredient_step_pos";
    public static final String ARG_INGRESTEPBUNDLE = "ingredient_step_bundle";
    public static final String PLAYING_POSITION = "playing_position";
    public static final String WAS_PLAYING = "was_playing";

    private static final String TAG = RecipeDetailFragment.class.getSimpleName();
    /**
     * The dummy content this fragment is presenting.
     */
    private ArrayList<IngreStep> ingreStepArrayList;
    private int ingreStepPos;
    private IngreStep ingreStep;

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private long playPosition;
    private boolean wasPlaying = true;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            initializeMediaSession();

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

    private int obtainIngreStepViewType() {
        Bundle bundle = getArguments();
        ArrayList<IngreStep> ingreStepArrayList = bundle.getParcelableArrayList(ARG_INGRESTEPLIST);
        int ingreStepPos = bundle.getInt(ARG_INGRESTEPPOS);
        return ingreStepArrayList.get(ingreStepPos).getViewType();
    }

    private String obtainVideoURLFromIntent() {
        Bundle bundle = getArguments();
        ArrayList<IngreStep> ingreStepArrayList = bundle.getParcelableArrayList(ARG_INGRESTEPLIST);
        int ingreStepPos = bundle.getInt(ARG_INGRESTEPPOS);
        return ingreStepArrayList.get(ingreStepPos).getVideoURL();
    }

    /**
     * Initializes the Media Session to be enabled with media buttons, transport controls, callbacks
     * and media controller.
     */
    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());


        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

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
            mExoPlayer.addListener(this);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(this.getContext(), getString(R.string.BakingOnClick));
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    this.getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
        }
        mExoPlayer.seekTo(playPosition);
        mExoPlayer.setPlayWhenReady(wasPlaying);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mExoPlayer != null) {
            playPosition = mExoPlayer.getCurrentPosition();
            wasPlaying = mExoPlayer.getPlayWhenReady();
            outState.putLong(PLAYING_POSITION, playPosition);
            outState.putBoolean(WAS_PLAYING, wasPlaying);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            playPosition = savedInstanceState.getLong(PLAYING_POSITION);
            wasPlaying = savedInstanceState.getBoolean(WAS_PLAYING);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (obtainIngreStepViewType() == IngreStep.VIEW_TYPE_STEP) {
            initializePlayer(Uri.parse(obtainVideoURLFromIntent()));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mExoPlayer.setPlayWhenReady(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null) {
            releasePlayer();
        }
        mMediaSession.setActive(false);
    }


    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    /**
     * Method that is called when the ExoPlayer state changes. Used to update the MediaSession
     * PlayBackState to keep in sync.
     *
     * @param playWhenReady true if ExoPlayer is playing, false if it's paused.
     * @param playbackState int describing the state of ExoPlayer. Can be STATE_READY, STATE_IDLE,
     *                      STATE_BUFFERING, or STATE_ENDED.
     */
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }

    /**
     * Media Session Callbacks, where all external clients control the player.
     */
    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }
}

package com.kwakdevs.kwak123.mybakingapp.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
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
import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.model.ModelTags;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.data.model.Step;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepository;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepositoryImpl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ExoPlayer code derived from the Udacity Android Developer Nanodegree Classic Music Quiz App example
 */

public class StepFragment extends Fragment implements ExoPlayer.EventListener,
        Transition.TransitionListener{
    private static final String LOG_TAG = StepFragment.class.getSimpleName();

    public static final String STEP_FRAGMENT_TAG = "step";

    private Unbinder unbinder;
    private int recipePosition;
    private int stepPosition;
    private Step step;
    private MediaSessionCompat mediaSessionCompat;
    private PlaybackStateCompat.Builder stateBuilder;
    private SimpleExoPlayer exoPlayer;

    @BindView(R.id.step_linear_layout) LinearLayout stepLinearLayout;
    @BindView(R.id.step_exoplayer_frame_layout) FrameLayout exoFrameLayout;
    @BindView(R.id.step_exoplayer_view) SimpleExoPlayerView simpleExoPlayerView;
    @BindView(R.id.step_description) TextView stepDescriptionTextView;

    public static StepFragment newInstance(int recipeIndex, int stepIndex) {
        StepFragment stepFragment = new StepFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ModelTags.TAG_RECIPE, recipeIndex);
        bundle.putInt(ModelTags.TAG_STEP, stepIndex);
        stepFragment.setArguments(bundle);
        return stepFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Transition t = TransitionInflater.from(getContext()).inflateTransition(
                R.transition.slide_right_fade);
        t.addListener(this);
        setEnterTransition(t);
        setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.quick_slide_right_together));


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        this.recipePosition = bundle.getInt(ModelTags.TAG_RECIPE);
        this.stepPosition = bundle.getInt(ModelTags.TAG_STEP);

        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        BakingRepository bakingRepository = BakingRepositoryImpl.getInstance();
        this.step = bakingRepository.getRecipe(recipePosition)
                .getSteps()
                .get(stepPosition);

        stepDescriptionTextView.setText(step.getDescription());
        stepLinearLayout.setTransitionGroup(true);

        stepLinearLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                stepLinearLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = stepLinearLayout.getMeasuredWidth();
                exoFrameLayout.setMinimumHeight(width * 9 / 16);
                exoFrameLayout.getLayoutParams().height = width * 9 / 16;
                exoFrameLayout.invalidate();
            }
        });

        initializeMediaSession();
        initializePlayer(step.getVideoUrl());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ModelTags.TAG_RECIPE, recipePosition);
        outState.putInt(ModelTags.TAG_STEP, stepPosition);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        releasePlayer();
        releaseMediaSession();
    }

    private void initializeMediaSession() {
        mediaSessionCompat = new MediaSessionCompat(getContext(), LOG_TAG);

        mediaSessionCompat.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mediaSessionCompat.setMediaButtonReceiver(null);

        stateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mediaSessionCompat.setPlaybackState(stateBuilder.build());
        mediaSessionCompat.setCallback(new SessionCallback());
        mediaSessionCompat.setActive(true);
    }

    private void initializePlayer(String uriString) {
        if (exoPlayer == null) {
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), selector, loadControl);
            simpleExoPlayerView.setPlayer(exoPlayer);

            if (!uriString.isEmpty()) {
                Uri mediaUri = Uri.parse(uriString);
                exoPlayer.addListener(this);

                String userAgent = Util.getUserAgent(getContext(), "MyBakingApp");
                MediaSource mediaSource = new ExtractorMediaSource(mediaUri,
                        new DefaultDataSourceFactory(getContext(), userAgent),
                        new DefaultExtractorsFactory(),
                        null,
                        null);
                exoPlayer.setPlayWhenReady(true);
                exoPlayer.prepare(mediaSource);
            } else {
                simpleExoPlayerView.setUseArtwork(true);
                simpleExoPlayerView.setUseController(false);
            }
        }
    }

    private void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    private void releaseMediaSession() {
        if (mediaSessionCompat != null) {
            mediaSessionCompat.setActive(false);
        }
    }

    @Override
    public void onTransitionEnd(Transition transition) {    }

    @Override
    public void onTransitionStart(Transition transition) {}

    @Override
    public void onTransitionCancel(Transition transition) {}

    @Override
    public void onTransitionPause(Transition transition) {}

    @Override
    public void onTransitionResume(Transition transition) {}

    // ExoPlayer Event Listeners
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
            stateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    exoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == ExoPlayer.STATE_READY)){
            stateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    exoPlayer.getCurrentPosition(), 1f);
        }
        mediaSessionCompat.setPlaybackState(stateBuilder.build());
    }

    // Unused Listener methods
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    private class SessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            exoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            exoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSeekTo(long pos) {
            exoPlayer.seekTo(pos);
        }

        @Override
        public void onSkipToPrevious() {
            super.onSkipToPrevious();
        }
    }
}
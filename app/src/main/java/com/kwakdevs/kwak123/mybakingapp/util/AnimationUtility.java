package com.kwakdevs.kwak123.mybakingapp.util;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.airbnb.lottie.LottieAnimationView;

/**
 * <p>Helper class for all animations.
 * Views declared explicitly, since each method has a narrow utility.</p>
 * <br/>
 * Thanks again,
 * <a href = "https://stackoverflow.com/questions/8063466/how-to-expand-a-layout-height-with-animation">Stack Overflow</a>
 */
public final class AnimationUtility {
    private static final String LOG_TAG = AnimationUtility.class.getSimpleName();
    private AnimationUtility(){}

    /**
     * Rotates the arrow used in an expandable list. Arrow points down by default
     *
     * @param imageView arrow to be rotated
     * @param isVisible whether or not the expandable list is visible
     */
    public static void rotateArrow(ImageView imageView, boolean isVisible) {
        int start = 0;
        int finish= 0;
        if (isVisible) {
            finish = 180;
        } else {
            start = 180;
        }
        int centerX = imageView.getWidth() / 2;
        int centerY = imageView.getHeight() / 2;
        int duration = 150;
        Animation an = new RotateAnimation(start, finish, centerX, centerY);
        an.setDuration(duration);
        an.setFillAfter(true);
        imageView.startAnimation(an);
    }

    /**
     * Animate expansion of a CardView with proper transition and fade
     *
     * @param expandableList LinearLayout that is being expanded
     * @param isVisible Whether or not expandableList is visible or not
     */
    public static void expandCardView(RecyclerView expandableList, boolean isVisible) {
        ExpandAnimatorListener listener = new ExpandAnimatorListener(expandableList, isVisible);
        expandableList.animate()
                .alpha(isVisible ? 0.0f : 1.0f)
                .setListener(listener);
    }

    /**
     * Useful for the bottom-most CardView. Properly animates and transitions expandableList, then
     * directs screen to the bottom of the associated ScrollView
     *
     * @param scrollView Root ScrollView
     * @param expandableList LinearLayout that is being expanded
     * @param isVisible Whether or not expandableList is visible
     */
    public static void expandCardView(ScrollView scrollView, RecyclerView expandableList, boolean isVisible) {
        ExpandAnimatorListener listener = new ExpandAnimatorListener(scrollView, expandableList, isVisible);
        expandableList.animate()
                .alpha(isVisible ? 0.0f : 1.0f)
                .setListener(listener);
    }

    /**
     * Fetches a new FavoritesClickListener.
     * @param context context
     * @param name recipe name
     * @return constructed FavoritesClickListener
     */
    public static FavoritesClickListener getFavoritesListener(Context context, String name) {
        return new FavoritesClickListener(context, name);
    }

    /**
     * Used with expandCardView methods. Animates visibility of the expanded views.
     */
    private static class ExpandAnimatorListener implements Animator.AnimatorListener {
        private ScrollView scrollView;
        private RecyclerView layout;
        private boolean isVisible;

        ExpandAnimatorListener(RecyclerView layout, boolean isVisible) {
            this.layout = layout;
            this.isVisible = isVisible;
        }

        ExpandAnimatorListener(ScrollView scrollView, RecyclerView layout, boolean isVisible) {
            this.scrollView = scrollView;
            this.layout = layout;
            this.isVisible = isVisible;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (!isVisible) layout.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (isVisible) layout.setVisibility(View.GONE);
            if (!isVisible && scrollView != null) scrollView.fullScroll(View.FOCUS_DOWN);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

    /**
     * Controls the Favorites animation used in the main screen
     */
    private static class FavoritesClickListener implements View.OnClickListener {

        private Context context;
        private String name;

        FavoritesClickListener(Context context, String name){
            this.context = context;
            this.name = name;
        }

        @Override
        public void onClick(View v) {
            LottieAnimationView animV = (LottieAnimationView) v;
            animV.setSpeed(1.75f);
            if (animV.getProgress() == 0f) {
                animV.playAnimation();
                SharedPrefsUtility.addFavorite(context, name);
            } else {
                animV.resumeReverseAnimation();
                SharedPrefsUtility.removeFavorite(context, name);
            }
        }
    }
}

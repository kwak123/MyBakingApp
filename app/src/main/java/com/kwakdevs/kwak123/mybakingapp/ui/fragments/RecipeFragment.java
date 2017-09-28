package com.kwakdevs.kwak123.mybakingapp.ui.fragments;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.model.ModelTags;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.ui.FragmentCallback;
import com.kwakdevs.kwak123.mybakingapp.ui.adapters.IngredientsAdapter;
import com.kwakdevs.kwak123.mybakingapp.ui.adapters.StepsAdapter;
import com.kwakdevs.kwak123.mybakingapp.ui.presenters.RecipePresenter;
import com.kwakdevs.kwak123.mybakingapp.ui.presenters.RecipePresenterImpl;
import com.kwakdevs.kwak123.mybakingapp.ui.views.RecipeView;
import com.kwakdevs.kwak123.mybakingapp.util.AnimationUtility;
import com.kwakdevs.kwak123.mybakingapp.util.SharedPrefsUtility;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RecipeFragment extends Fragment implements RecipeView {
    private static final String LOG_TAG = RecipeFragment.class.getSimpleName();

    public static final String RECIPE_FRAGMENT_TAG = "recipe_fragment";
    public static final String SHARED_CARD_TAG = "shared_card";

    private RecipePresenter presenter;
    private FragmentCallback callback;
    private Unbinder unbinder;
    private int recipePosition;
    private String transitionName;

    @BindView(R.id.recipe_scroll_view) ScrollView recipeScrollView;
    @BindView(R.id.recipe_main_layout) LinearLayout recipeMainLayout;
    @BindView(R.id.overview_card_view) CardView overviewCardView;
    @BindView(R.id.recipe_name_text_view) TextView recipeNameTextView;
    @BindView(R.id.main_favorite_icon) LottieAnimationView favoriteAnimationView;
    @BindView(R.id.total_servings_text_view) TextView totalServingsTextView;
    @BindView(R.id.total_ingredients_text_view) TextView totalIngredientsTextView;
    @BindView(R.id.total_steps_text_view) TextView totalStepsTextView;
    @BindView(R.id.ingredients_card_view) CardView ingredientsCardView;
    @BindView(R.id.steps_card_view) CardView stepsCardView;
    @BindView(R.id.ingredients_header_view) TextView ingredientsHeaderView;
    @BindView(R.id.steps_header_view) TextView stepsHeaderView;
    @BindView(R.id.ing_expand_image_view) ImageView ingredientsExpandArrow;
    @BindView(R.id.step_expand_image_view) ImageView stepsExpandArrow;
    @BindView(R.id.ingredients_list_layout) RecyclerView ingredientsListLayout;
    @BindView(R.id.steps_list_layout) RecyclerView stepsListLayout;

    public static RecipeFragment newInstance(int recipeIndex, String transitionName) {
        RecipeFragment recipeFragment = new RecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ModelTags.TAG_RECIPE, recipeIndex);
        bundle.putString(SHARED_CARD_TAG, transitionName);
        recipeFragment.setArguments(bundle);
        return recipeFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getActivity()).inflateTransition(
                R.transition.delayed_move));
        setEnterTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.delayed_slide_right));
        setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.delayed_slide_left));
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.quick_slide_left_together));
        setReturnTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.quick_slide_right));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        this.recipePosition = bundle.getInt(ModelTags.TAG_RECIPE);
        this.transitionName = bundle.getString(SHARED_CARD_TAG);
        presenter = new RecipePresenterImpl(this);
        Recipe recipe = presenter.getRecipe(recipePosition);

        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter(getContext(), recipe.getIngredients());
        ingredientsListLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsListLayout.setAdapter(ingredientsAdapter);

        StepsAdapter stepsAdapter = new StepsAdapter(getContext(), recipe.getSteps(), (StepsAdapter.Listener) presenter);
        stepsListLayout.setLayoutManager(new LinearLayoutManager(getContext()));
        stepsListLayout.setAdapter(stepsAdapter);

        overviewCardView.setTransitionName(transitionName);

        LayoutTransition mainTransition = new LayoutTransition();
        mainTransition.enableTransitionType(LayoutTransition.CHANGING);
        recipeMainLayout.setLayoutTransition(mainTransition);

        ingredientsHeaderView.setOnClickListener(new IngCardExpandListener());
        stepsHeaderView.setOnClickListener(new StepCardExpandListener());

        buildOverviewCardView(recipe);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        callback = (FragmentCallback) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.release();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ModelTags.TAG_RECIPE, recipePosition);
        outState.putString(SHARED_CARD_TAG, transitionName);
    }

    private void buildOverviewCardView(Recipe recipe) {
        String recipeName = recipe.getName();
        String recipeTotalServings = getString(R.string.recipe_serves) + recipe.getServings();
        String recipeTotalIngredients = getString(R.string.recipe_total_ingredients) + recipe.getIngredients().size();
        String recipeTotalSteps = getString(R.string.recipe_total_steps) + recipe.getSteps().size();
        recipeNameTextView.setText(recipeName);
        totalServingsTextView.setText(recipeTotalServings);
        totalIngredientsTextView.setText(recipeTotalIngredients);
        totalStepsTextView.setText(recipeTotalSteps);
        if (SharedPrefsUtility.checkFavorite(getContext(), recipeName)) {
            favoriteAnimationView.setProgress(1.0f);
        }
        favoriteAnimationView.setOnClickListener(AnimationUtility
                .getFavoritesListener(getContext(), recipeName));
    }

    // RecipeView methods
    @Override
    public void onStepClicked(int stepPosition) {
        callback.loadStepFragment(recipePosition, stepPosition);
    }

    // OnClickListeners
    private class IngCardExpandListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            final boolean isVisible = ingredientsListLayout.getVisibility() == View.VISIBLE;
            AnimationUtility.expandCardView(ingredientsListLayout, isVisible);
            AnimationUtility.rotateArrow(ingredientsExpandArrow, isVisible);
        }
    }

    private class StepCardExpandListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            boolean isVisible = stepsListLayout.getVisibility() == View.VISIBLE;
            AnimationUtility.expandCardView(recipeScrollView, stepsListLayout, isVisible);
            AnimationUtility.rotateArrow(stepsExpandArrow, isVisible);
        }
    }
}
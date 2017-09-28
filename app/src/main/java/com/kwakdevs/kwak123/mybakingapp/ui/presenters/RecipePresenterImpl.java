package com.kwakdevs.kwak123.mybakingapp.ui.presenters;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepository;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepositoryImpl;
import com.kwakdevs.kwak123.mybakingapp.ui.adapters.StepsAdapter;
import com.kwakdevs.kwak123.mybakingapp.ui.views.RecipeView;

public class RecipePresenterImpl implements RecipePresenter,
        StepsAdapter.Listener{
    private static final String LOG_TAG = RecipePresenterImpl.class.getSimpleName();

    private RecipeView recipeView;
    private BakingRepository bakingRepository;

    public RecipePresenterImpl(RecipeView recipeView) {
        this.recipeView = recipeView;
        this.bakingRepository = BakingRepositoryImpl.getInstance();
    }

    @Override
    public Recipe getRecipe(int recipePosition) {
        return bakingRepository.getRecipe(recipePosition);
    }

    @Override
    public void release() {
        recipeView = null;
        bakingRepository = null;
    }

    @Override
    public void onStepClicked(int stepPosition) {
        recipeView.onStepClicked(stepPosition);
    }
}

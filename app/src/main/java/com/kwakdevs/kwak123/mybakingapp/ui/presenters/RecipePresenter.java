package com.kwakdevs.kwak123.mybakingapp.ui.presenters;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

public interface RecipePresenter {
    Recipe getRecipe(int recipePosition);

    void release();
}

package com.kwakdevs.kwak123.mybakingapp.ui.views;

import android.view.View;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.util.List;

public interface MainView {
    void bindRecipes(List<Recipe> recipes);
    void onRecipeClicked(int recipePosition, View sharedView);
}

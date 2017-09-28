package com.kwakdevs.kwak123.mybakingapp.ui.presenters;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.util.List;

public interface MainPresenter {
    List<Recipe> getRecipes();
    void release();
}

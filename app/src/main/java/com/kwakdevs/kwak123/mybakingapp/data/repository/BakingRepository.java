package com.kwakdevs.kwak123.mybakingapp.data.repository;

import android.support.annotation.NonNull;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.util.List;

public interface BakingRepository {

    void bindRecipes(List<Recipe> recipes);

    void bindListener(Listener listener, String tag);

    void notifyCallbacks();

    void releaseListener(String tag);

    @NonNull
    List<Recipe> getRecipes();

    @NonNull
    Recipe getRecipe(int position);

    interface Listener {
        void onUpdate(List<Recipe> recipes);
    }
}

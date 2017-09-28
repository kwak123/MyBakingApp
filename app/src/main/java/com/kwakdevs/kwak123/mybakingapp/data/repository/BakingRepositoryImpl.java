package com.kwakdevs.kwak123.mybakingapp.data.repository;

import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public final class BakingRepositoryImpl implements BakingRepository {
    private static final String LOG_TAG = BakingRepositoryImpl.class.getSimpleName();

    private static BakingRepository bakingRepository;
    private static ArrayMap<String, Listener> callbacks;
    private static ArrayList<String> callbackTags;

    private List<Recipe> recipes;

    private BakingRepositoryImpl(){}

    public static BakingRepository getInstance() {
        if (bakingRepository == null) bakingRepository = new BakingRepositoryImpl();
        if (callbacks == null) callbacks = new ArrayMap<>();
        if (callbackTags == null) callbackTags = new ArrayList<>();
        return bakingRepository;
    }



    @Override
    public void bindRecipes(@NonNull List<Recipe> recipes) {
        this.recipes = recipes;
        notifyCallbacks();
    }

    @Override
    public void bindListener(@NonNull Listener listener, @NonNull String tag) {
        callbacks.put(tag, listener);
        callbackTags.add(tag);
    }

    @Override
    public void notifyCallbacks() {
        if (callbacks == null || callbacks.isEmpty()) return;
        for (int i = 0; i < callbackTags.size(); i++) {
            callbacks.get(callbackTags.get(i)).onUpdate(recipes);
        }
    }

    @Override
    public void releaseListener(String tag) {
        callbacks.remove(tag);
    }

    @NonNull
    @Override
    public List<Recipe> getRecipes() {
        if (recipes == null) recipes = new ArrayList<>();
        return recipes;
    }

    @NonNull
    @Override
    public Recipe getRecipe(int position) {
        if (recipes == null || recipes.isEmpty()) return Recipe.getEmptyRecipe();

        Recipe recipe;

        if (recipes.get(position) == null) {
            recipe = Recipe.getEmptyRecipe();
        } else {
            recipe = recipes.get(position);
        }

        return recipe;
    }
}
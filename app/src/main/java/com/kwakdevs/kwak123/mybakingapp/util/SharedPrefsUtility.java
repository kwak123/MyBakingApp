package com.kwakdevs.kwak123.mybakingapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

/**
 * Handles management of favorite items by name
 */
public final class SharedPrefsUtility {
    private static final String LOG_TAG = SharedPrefsUtility.class.getSimpleName();
    private SharedPrefsUtility(){}

    private static final String PREF_NAME = "baking_prefs";
    private static final int PREF_MODE = 0;
    private static final String KEY_RECIPES = "recipes";
    private static final String KEY_AGE = "age";

    private static final int MAX_AGE = 1000 * 60 * 60 * 24 * 7; // 1 week

    static void addFavorite(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        preferences.edit()
                .putString(name, "")
                .apply();
    }

    static void removeFavorite(Context context, String name) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        preferences.edit()
                .remove(name)
                .apply();
    }

    public static boolean checkFavorite(Context context, String name) {
        return context.getSharedPreferences(PREF_NAME, PREF_MODE).contains(name);
    }

    public static void saveRecipes(Context context, List<Recipe> recipes) {
        if (recipes == null || recipes.isEmpty()) return;
        Gson gson = new Gson();
        String recipesJson = gson.toJson(recipes);
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        preferences.edit()
                .putString(KEY_RECIPES, recipesJson)
                .apply();
    }

    @NonNull
    public static List<Recipe> getRecipes(Context context) {
        String recipesJson = context.getSharedPreferences(PREF_NAME, PREF_MODE)
                .getString(KEY_RECIPES, "");
        if (recipesJson.isEmpty()) return new ArrayList<>(); // Exit here if nothing found

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Recipe>>(){}.getType();
        return gson.fromJson(recipesJson, type);
    }

    /**
     * Check age in SharedPref
     * @param context Get SharedPrefs
     * @return  Returns true if beyond max age or if data is being saved for the first time
     */
    public static boolean beyondMaxAge(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, PREF_MODE);
        long currentTime = System.currentTimeMillis();
        long storedTime = preferences.getLong(KEY_AGE, 0);
        if (currentTime - storedTime > MAX_AGE) {
            preferences.edit()
                    .putLong(KEY_AGE,currentTime)
                    .apply();
            return true;
        } else {
            return false;
        }
    }
}
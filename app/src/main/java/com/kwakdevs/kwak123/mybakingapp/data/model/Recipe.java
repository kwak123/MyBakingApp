package com.kwakdevs.kwak123.mybakingapp.data.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public final class Recipe {
    public static final String EMPTY_RECIPE = "empty";

    @SerializedName("id")
    private Integer id;

    @SerializedName("name")
    private String name;

    @SerializedName("ingredients")
    private ArrayList<Ingredient> ingredients;

    @SerializedName("steps")
    private ArrayList<Step> steps;

    @SerializedName("servings")
    private Integer servings;

    @SerializedName("image")
    private String image;

    public Recipe(Integer id, String name, ArrayList<Ingredient> ingredients, ArrayList<Step> steps,
                  Integer servings, String image) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public Integer getId() {
        return this.id;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public ArrayList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    public ArrayList<Step> getSteps() {
        return this.steps;
    }

    public Integer getServings() {
        return this.servings;
    }

    public String getImage() {
        return this.image;
    }

    public static Recipe getEmptyRecipe() {
        return new Recipe(0, EMPTY_RECIPE, new ArrayList<Ingredient>(), new ArrayList<Step>(),
                0, "");
    }
}

package com.kwakdevs.kwak123.mybakingapp.data.retrofit;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface BakingInterface {
    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}

package com.kwakdevs.kwak123.mybakingapp.data.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.util.SharedPrefsUtility;

import java.util.List;

public class RecipeLoader extends AsyncTaskLoader<List<Recipe>> {

    private Context context;
    private List<Recipe> data;

    public RecipeLoader(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public List<Recipe> loadInBackground() {
        data = SharedPrefsUtility.getRecipes(context);
        return data;
    }

    @Override
    public void deliverResult(List<Recipe> data) {
        if (isReset()) {
            onReleaseResources(data);
            return;
        }

        List<Recipe> oldData = this.data;
        this.data = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldData != null && oldData != data) {
            onReleaseResources(oldData);
        }
    }

    private void onReleaseResources(List<Recipe> data){}
}

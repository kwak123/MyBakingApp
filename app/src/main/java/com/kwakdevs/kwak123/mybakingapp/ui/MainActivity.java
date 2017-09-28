package com.kwakdevs.kwak123.mybakingapp.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.loader.RecipeLoader;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepository;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepositoryImpl;
import com.kwakdevs.kwak123.mybakingapp.data.retrofit.BakingClient;
import com.kwakdevs.kwak123.mybakingapp.data.retrofit.BakingInterface;
import com.kwakdevs.kwak123.mybakingapp.ui.fragments.MainFragment;
import com.kwakdevs.kwak123.mybakingapp.ui.fragments.RecipeFragment;
import com.kwakdevs.kwak123.mybakingapp.ui.fragments.StepFragment;
import com.kwakdevs.kwak123.mybakingapp.util.SharedPrefsUtility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kwakdevs.kwak123.mybakingapp.ui.fragments.MainFragment.MAIN_FRAGMENT_TAG;

public class MainActivity extends AppCompatActivity implements FragmentCallback,
        LoaderManager.LoaderCallbacks<List<Recipe>> {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String STARTED_TAG = "isStarted";

    private BakingRepository bakingRepository;

    private boolean isStarted = false;
    private boolean isTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) isStarted = savedInstanceState.getBoolean(STARTED_TAG);

        setContentView(R.layout.activity_main);
        bakingRepository = BakingRepositoryImpl.getInstance();

        if (!isStarted) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_layout,
                            MainFragment.newInstance(),
                            MAIN_FRAGMENT_TAG)
                    .commit();
            isStarted = true;
        }

        if (SharedPrefsUtility.beyondMaxAge(this)) {
            Log.d(LOG_TAG, "You smell that? Better heat up the oven");
            BakingInterface bakingService = BakingClient.getClient(this).create(BakingInterface.class);
            Call<List<Recipe>> call = bakingService.getRecipes();
            call.enqueue(new BakingCallback(this));
        } else {
            Log.d(LOG_TAG, "That bagel's not stale yet");
            getLoaderManager().initLoader(0, null, this).forceLoad();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STARTED_TAG, isStarted);
    }

    @Override
    public Loader<List<Recipe>> onCreateLoader(int id, Bundle args) {
        return new RecipeLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Recipe>> loader, List<Recipe> data) {
        bakingRepository.bindRecipes(data);
    }

    @Override
    public void onLoaderReset(Loader<List<Recipe>> loader) {}

    // Fragment Callbacks
    @Override
    public void loadRecipeFragment(int adapterPosition, View sharedView) {
        String transitionName = ViewCompat.getTransitionName(sharedView);
        RecipeFragment recipeFragment = RecipeFragment.newInstance(adapterPosition, transitionName);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(MainFragment.MAIN_FRAGMENT_TAG)
                .addSharedElement(sharedView, ViewCompat.getTransitionName(sharedView))
                .replace(R.id.main_layout, recipeFragment, RecipeFragment.RECIPE_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void loadStepFragment(int recipeIndex, int stepIndex) {
        StepFragment stepFragment = StepFragment.newInstance(recipeIndex, stepIndex);
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(RecipeFragment.RECIPE_FRAGMENT_TAG)
                .replace(R.id.main_layout, stepFragment, StepFragment.STEP_FRAGMENT_TAG)
                .commit();
    }

    private class BakingCallback implements Callback<List<Recipe>> {

        private Context context;

        BakingCallback(Context context) {
            this.context = context;
        }

        @Override
        public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
            List<Recipe> recipes = response.body();
            bakingRepository.bindRecipes(recipes);
            SharedPrefsUtility.saveRecipes(context, recipes); // Save recipes async as cache for later
        }

        @Override
        public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
            Toast.makeText(context, "Failed to retrieve recipes!", Toast.LENGTH_LONG)
                    .show();
            Log.e(LOG_TAG, t.getMessage());
        }
    }
}
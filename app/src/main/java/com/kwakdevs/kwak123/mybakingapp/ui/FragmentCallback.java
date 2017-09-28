package com.kwakdevs.kwak123.mybakingapp.ui;

import android.view.View;

public interface FragmentCallback {
    void loadRecipeFragment(int adapterPosition, View sharedView);
    void loadStepFragment(int recipeIndex, int stepIndex);
}

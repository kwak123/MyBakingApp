package com.kwakdevs.kwak123.mybakingapp.ui.presenters;

import android.view.View;

import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepository;
import com.kwakdevs.kwak123.mybakingapp.data.repository.BakingRepositoryImpl;
import com.kwakdevs.kwak123.mybakingapp.ui.adapters.MainAdapter;
import com.kwakdevs.kwak123.mybakingapp.ui.views.MainView;

import java.util.List;

public class MainPresenterImpl implements MainPresenter,
        MainAdapter.Listener,
        BakingRepository.Listener {
    private static final String LOG_TAG = MainPresenterImpl.class.getSimpleName();
    private static final String LISTENER_TAG = "main_presenter";

    private MainView mainView;
    private BakingRepository bakingRepository;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        this.bakingRepository = BakingRepositoryImpl.getInstance();
        bakingRepository.bindListener(this, LISTENER_TAG);
    }

    @Override
    public List<Recipe> getRecipes() {
        return bakingRepository.getRecipes();
    }

    @Override
    public void release() {
        bakingRepository.releaseListener(LISTENER_TAG);
        bakingRepository = null;
        mainView = null;
    }

    // MainAdapter.Listener Methods
    @Override
    public void onRecipeClicked(int adapterPosition, View sharedView) {
        mainView.onRecipeClicked(adapterPosition, sharedView);
    }

    // BakingRepository.Listener methods
    @Override
    public void onUpdate(List<Recipe> recipes) {
        mainView.bindRecipes(recipes);
    }

}

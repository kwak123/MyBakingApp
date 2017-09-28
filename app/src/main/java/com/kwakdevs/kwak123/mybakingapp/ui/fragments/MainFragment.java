package com.kwakdevs.kwak123.mybakingapp.ui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.ui.FragmentCallback;
import com.kwakdevs.kwak123.mybakingapp.ui.adapters.MainAdapter;
import com.kwakdevs.kwak123.mybakingapp.ui.presenters.MainPresenter;
import com.kwakdevs.kwak123.mybakingapp.ui.presenters.MainPresenterImpl;
import com.kwakdevs.kwak123.mybakingapp.ui.views.MainView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Weird action scrolling down?
 */

public class MainFragment extends Fragment implements MainView {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    public static final String MAIN_FRAGMENT_TAG = "main";

    @BindView(R.id.main_recycler_view) RecyclerView recyclerView;

    private MainPresenter presenter;
    private FragmentCallback callback;
    private Unbinder unbinder;
    private MainAdapter adapter;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setReenterTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.delayed_slide_left));
        setExitTransition(TransitionInflater.from(getContext()).inflateTransition(
                R.transition.quick_slide_left));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        this.presenter = new MainPresenterImpl(this);

        // Adapter awaits data, presenter needs to implement listener
        adapter = new MainAdapter(getActivity(), (MainAdapter.Listener) presenter);
        adapter.bindRecipes(presenter.getRecipes());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        getActivity().setTitle(getString(R.string.app_name));
        return rootView;
    }

    // Expect to crash if activity does not implement callback, that would be incorrect behavior
    @Override
    public void onStart() {
        super.onStart();
        callback = (FragmentCallback) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        presenter.release();
    }

    @Override
    public void bindRecipes(List<Recipe> recipes) {
        adapter.bindRecipes(recipes);
    }

    @Override
    public void onRecipeClicked(int adapterPosition, View sharedView) {
        callback.loadRecipeFragment(adapterPosition, sharedView);
    }
}
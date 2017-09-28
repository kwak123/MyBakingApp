package com.kwakdevs.kwak123.mybakingapp.ui.adapters;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.kwakdevs.kwak123.mybakingapp.R;
import com.kwakdevs.kwak123.mybakingapp.data.model.Recipe;
import com.kwakdevs.kwak123.mybakingapp.util.AnimationUtility;
import com.kwakdevs.kwak123.mybakingapp.util.SharedPrefsUtility;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
    private static final String LOG_TAG = MainAdapter.class.getSimpleName();

    private List<Recipe> recipes;
    private Context context;
    private Listener listener;

    public MainAdapter(Context context, Listener listener){
        this.context = context;
        this.listener = listener;
    }


    @Override
    public MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_recipe_view, parent, false);
        return new MainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MainViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        String recipeName = recipe.getName();
        String recipeTotalServings = context.getString(R.string.recipe_serves) +
                recipe.getServings();
        String recipeTotalIngredients = context.getString(R.string.recipe_total_ingredients) +
                recipe.getIngredients().size();
        String recipeTotalSteps = context.getString(R.string.recipe_total_steps) +
                recipe.getSteps().size();

        ViewCompat.setTransitionName(holder.recipeCardView, recipeName);

        if (SharedPrefsUtility.checkFavorite(context, recipeName)) {
            holder.favoriteAnimationView.setProgress(1.0f);
        }
        holder.favoriteAnimationView.setOnClickListener(AnimationUtility
                .getFavoritesListener(context, recipeName));

        // Add bottom margin if last item
        if (position == recipes.size()-1) {
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            int defaultMargin = params.getMarginStart();
            params.setMargins(defaultMargin,
                    defaultMargin,
                    defaultMargin,
                    defaultMargin);
            holder.itemView.setLayoutParams(params);
        }

        // Add tag to CardView?
        holder.itemView.setTag(position);

        holder.recipeNameTextView.setText(recipeName);
        holder.totalServingsTextView.setText(recipeTotalServings);
        holder.totalIngredientsTextView.setText(recipeTotalIngredients);
        holder.totalStepsTextView.setText(recipeTotalSteps);

    }

    public void bindRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (recipes == null) return 0;
        return recipes.size();
    }


    class MainViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.recipe_card_view) CardView recipeCardView;
        @BindView(R.id.recipe_name_text_view) TextView recipeNameTextView;
        @BindView(R.id.main_favorite_icon) LottieAnimationView favoriteAnimationView;
        @BindView(R.id.total_servings_text_view) TextView totalServingsTextView;
        @BindView(R.id.total_ingredients_text_view) TextView totalIngredientsTextView;
        @BindView(R.id.total_steps_text_view) TextView totalStepsTextView;

        MainViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecipeClicked(getAdapterPosition(), recipeCardView);
        }
    }

    /**
     * Adapter provides selected index
     */
    public interface Listener {
        void onRecipeClicked(int adapterPosition, View sharedView);
    }
}

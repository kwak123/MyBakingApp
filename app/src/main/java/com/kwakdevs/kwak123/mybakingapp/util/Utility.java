package com.kwakdevs.kwak123.mybakingapp.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;

import com.kwakdevs.kwak123.mybakingapp.data.model.Ingredient;
import com.kwakdevs.kwak123.mybakingapp.data.model.Step;

import static android.R.attr.id;
import static android.R.attr.name;

/**
 * Format data and provide information regarding state of the device
 */
public final class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    private Utility(){}

    private static final String TAG_CUP = "CUP";
    private static final String TAG_TBLSP = "TBLSP";
    private static final String TAG_TSP = "TSP";
    private static final String TAG_GRAM = "G";
    private static final String TAG_OZ = "OZ";
    private static final String TAG_UNIT = "UNIT";
    private static final String TAG_K = "K";

    @NonNull
    public static String formatIngredientName(@NonNull Ingredient ingredient) {
        StringBuilder ingredientNameBuilder = new StringBuilder();
        String name = ingredient.getName();
        ingredientNameBuilder.append(name.substring(0, 1).toUpperCase());
        ingredientNameBuilder.append(name.substring(1));
        return ingredientNameBuilder.toString();
    }

    /**
     * <p>Pass in ingredient, receive a legible quantity.<br>
     * e.g. "8.0 cups" </p>
     *
     * @param ingredient ingredient to parse
     * @return formatted String
     */
    @NonNull
    public static String formatIngredientQuantity(Ingredient ingredient) {
        if (ingredient == null) return "";
        StringBuilder ingredientQuantityBuilder = new StringBuilder();

        Float quantity = ingredient.getQuantity();
        String measure = ingredient.getMeasure();

        ingredientQuantityBuilder.append(quantity);
        ingredientQuantityBuilder.append(" ");

        switch (measure) {
            case TAG_CUP:
                ingredientQuantityBuilder.append("cup");
                break;
            case TAG_TBLSP:
                ingredientQuantityBuilder.append("tablespoon");
                break;
            case TAG_TSP:
                ingredientQuantityBuilder.append("teaspoon");
                break;
            case TAG_GRAM:
                ingredientQuantityBuilder.append("gram");
                break;
            case TAG_OZ:
                ingredientQuantityBuilder.append("ounce");
                break;
            case TAG_K:
                ingredientQuantityBuilder.append("kilogram");
                break;
            case TAG_UNIT:
                ingredientQuantityBuilder.append("");
                break;
            default:
                ingredientQuantityBuilder.append("error?");
                break;
        }

        if (quantity > 1 && !measure.equals(TAG_UNIT)) {
            ingredientQuantityBuilder.append("s");
        }

        return ingredientQuantityBuilder.toString();
    }

    /**
     * Handles properly constructing a step's short description
     * @param step Desired Step
     * @return Step's short description, formatted
     */
    @NonNull
    public static String formatStepShort(@NonNull Step step) {
        StringBuilder builder = new StringBuilder();
        if (step.getId() != 0) {
            builder.append(Integer.toString(step.getId()));
            builder.append(": ");
        }
        String shortDesc = step.getShortDescription();
        builder.append(shortDesc);
        return builder.toString();
    }

    // TODO: Finish this
    public static boolean externalStoragePermission(Context context) {
        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }
}

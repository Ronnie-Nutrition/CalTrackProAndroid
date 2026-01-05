package com.easyaiflows.caltrackpro.ui.recipe

import com.easyaiflows.caltrackpro.domain.model.Recipe

/**
 * UI state for the Recipe Detail screen.
 */
data class RecipeDetailUiState(
    val recipe: Recipe? = null,
    val servingMultiplier: Double = 1.0,
    val isLoading: Boolean = true,
    val isDeleting: Boolean = false,
    val isAddingToDiary: Boolean = false,
    val error: String? = null
) {
    /**
     * Scaled calories based on serving multiplier.
     */
    val scaledCalories: Double
        get() = (recipe?.caloriesPerServing ?: 0.0) * servingMultiplier

    /**
     * Scaled protein based on serving multiplier.
     */
    val scaledProtein: Double
        get() = (recipe?.proteinPerServing ?: 0.0) * servingMultiplier

    /**
     * Scaled carbs based on serving multiplier.
     */
    val scaledCarbs: Double
        get() = (recipe?.carbsPerServing ?: 0.0) * servingMultiplier

    /**
     * Scaled fat based on serving multiplier.
     */
    val scaledFat: Double
        get() = (recipe?.fatPerServing ?: 0.0) * servingMultiplier

    /**
     * Display text for serving multiplier.
     */
    val servingMultiplierDisplay: String
        get() = if (servingMultiplier == servingMultiplier.toInt().toDouble()) {
            "${servingMultiplier.toInt()}×"
        } else {
            "${servingMultiplier}×"
        }

    /**
     * Whether recipe data is available.
     */
    val hasRecipe: Boolean
        get() = recipe != null

    /**
     * Scale an ingredient quantity by the serving multiplier.
     */
    fun scaleQuantity(originalQuantity: Double): Double {
        return originalQuantity * servingMultiplier
    }

    /**
     * Format scaled quantity for display.
     */
    fun formatScaledQuantity(originalQuantity: Double): String {
        val scaled = scaleQuantity(originalQuantity)
        return if (scaled == scaled.toLong().toDouble()) {
            scaled.toLong().toString()
        } else {
            String.format("%.1f", scaled)
        }
    }
}

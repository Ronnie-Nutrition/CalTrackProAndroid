package com.easyaiflows.caltrackpro.domain.model

/**
 * Represents an ingredient in a recipe with its quantity.
 * Nutrition values are computed based on the quantity relative to the serving size.
 */
data class RecipeIngredient(
    val foodItem: SimpleFoodItem,
    val quantity: Double
) {
    /**
     * Calculated calories for this ingredient based on quantity.
     * Formula: (foodItem.calories * quantity) / foodItem.servingSize
     */
    val calories: Double
        get() = (foodItem.calories * quantity) / foodItem.servingSize

    /**
     * Calculated protein for this ingredient based on quantity.
     */
    val protein: Double
        get() = (foodItem.protein * quantity) / foodItem.servingSize

    /**
     * Calculated carbs for this ingredient based on quantity.
     */
    val carbs: Double
        get() = (foodItem.carbs * quantity) / foodItem.servingSize

    /**
     * Calculated fat for this ingredient based on quantity.
     */
    val fat: Double
        get() = (foodItem.fat * quantity) / foodItem.servingSize
}

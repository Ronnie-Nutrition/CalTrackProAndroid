package com.easyaiflows.caltrackpro.domain.model

/**
 * Domain model representing a food item from search results.
 * Contains nutrition data per 100g and available serving measures.
 */
data class SearchedFood(
    val foodId: String,
    val name: String,
    val brand: String?,
    val category: String?,
    val imageUrl: String?,

    // Nutrition per 100g
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double,
    val sugarPer100g: Double,
    val sodiumPer100g: Double,

    // Available serving measures
    val measures: List<ServingMeasure>
) {
    /**
     * Calculate nutrition for a specific serving.
     * @param measure The selected serving measure
     * @param quantity Number of servings
     * @return NutritionValues for the specified serving
     */
    fun calculateNutrition(measure: ServingMeasure, quantity: Double): NutritionValues {
        val weightInGrams = measure.weightGrams * quantity
        val multiplier = weightInGrams / 100.0

        return NutritionValues(
            calories = (caloriesPer100g * multiplier).toInt(),
            protein = proteinPer100g * multiplier,
            carbs = carbsPer100g * multiplier,
            fat = fatPer100g * multiplier,
            fiber = fiberPer100g * multiplier,
            sugar = sugarPer100g * multiplier,
            sodium = sodiumPer100g * multiplier
        )
    }

    /**
     * Get the default serving measure (first available, or "Serving" if exists).
     */
    val defaultMeasure: ServingMeasure?
        get() = measures.find { it.label.equals("Serving", ignoreCase = true) }
            ?: measures.firstOrNull()

    /**
     * Display name including brand if available.
     */
    val displayName: String
        get() = if (brand != null) "$name ($brand)" else name
}

/**
 * Calculated nutrition values for a specific serving.
 */
data class NutritionValues(
    val calories: Int,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double,
    val sugar: Double,
    val sodium: Double
)

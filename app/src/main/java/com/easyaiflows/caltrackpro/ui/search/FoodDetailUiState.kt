package com.easyaiflows.caltrackpro.ui.search

import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.domain.model.NutritionValues
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import com.easyaiflows.caltrackpro.domain.model.ServingMeasure

/**
 * UI state for the Food Detail screen
 */
data class FoodDetailUiState(
    val food: SearchedFood? = null,
    val selectedMeasure: ServingMeasure? = null,
    val quantity: Double = 1.0,
    val selectedMealType: MealType = MealType.SNACK,
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saveSuccess: Boolean = false
) {
    /**
     * Calculate nutrition values based on selected measure and quantity
     */
    val calculatedNutrition: NutritionValues?
        get() = food?.let { f ->
            selectedMeasure?.let { m ->
                f.calculateNutrition(m, quantity)
            }
        }

    /**
     * Display text for the current serving
     */
    val servingDisplay: String
        get() {
            val measureLabel = selectedMeasure?.label ?: "serving"
            return if (quantity == 1.0) {
                "1 $measureLabel"
            } else {
                "${quantity.let { if (it == it.toLong().toDouble()) it.toLong().toString() else String.format("%.1f", it) }} $measureLabel"
            }
        }

    /**
     * Check if the UI is in a valid state for adding to diary
     */
    val canSave: Boolean
        get() = food != null && selectedMeasure != null && quantity > 0 && !isSaving
}

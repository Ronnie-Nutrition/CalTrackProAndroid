package com.easyaiflows.caltrackpro.domain.model

/**
 * Simplified food item data for use in recipes.
 * Contains essential nutrition information per serving size.
 */
data class SimpleFoodItem(
    val name: String,
    val brand: String? = null,
    val barcode: String? = null,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val servingSize: Double,
    val servingUnit: String
)

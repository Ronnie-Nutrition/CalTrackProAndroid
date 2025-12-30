package com.easyaiflows.caltrackpro.data.remote.dto

import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import com.easyaiflows.caltrackpro.domain.model.ServingMeasure

/**
 * Extension functions for mapping DTOs to domain models.
 */

/**
 * Convert MeasureDto to ServingMeasure domain model.
 */
fun MeasureDto.toDomainModel(): ServingMeasure {
    return ServingMeasure(
        uri = uri,
        label = label,
        weightGrams = weight
    )
}

/**
 * Convert a list of MeasureDto to domain models.
 */
fun List<MeasureDto>.toDomainModels(): List<ServingMeasure> {
    return map { it.toDomainModel() }
}

/**
 * Convert FoodHintDto to SearchedFood domain model.
 * Combines food data with available measures.
 */
fun FoodHintDto.toDomainModel(): SearchedFood {
    val nutrients = food.nutrients
    return SearchedFood(
        foodId = food.foodId,
        name = food.label,
        brand = food.brand,
        category = food.category,
        imageUrl = food.image,
        caloriesPer100g = nutrients.calories ?: 0.0,
        proteinPer100g = nutrients.protein ?: 0.0,
        carbsPer100g = nutrients.carbohydrates ?: 0.0,
        fatPer100g = nutrients.fat ?: 0.0,
        fiberPer100g = nutrients.fiber ?: 0.0,
        sugarPer100g = nutrients.sugar ?: 0.0,
        sodiumPer100g = nutrients.sodium ?: 0.0,
        measures = measures.toDomainModels()
    )
}

/**
 * Convert a list of FoodHintDto to domain models.
 */
fun List<FoodHintDto>.toDomainModels(): List<SearchedFood> {
    return map { it.toDomainModel() }
}

/**
 * Convert FoodSearchResponseDto to a list of SearchedFood domain models.
 * Extracts foods from hints and parsed results.
 */
fun FoodSearchResponseDto.toDomainModels(): List<SearchedFood> {
    val hintsResults = hints?.toDomainModels() ?: emptyList()
    // Parsed results don't include measures, so we skip them for now
    // They represent exact matches which are also in hints
    return hintsResults
}

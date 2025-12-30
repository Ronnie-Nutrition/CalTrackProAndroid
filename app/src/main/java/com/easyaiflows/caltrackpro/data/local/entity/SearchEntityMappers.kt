package com.easyaiflows.caltrackpro.data.local.entity

import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import com.easyaiflows.caltrackpro.domain.model.ServingMeasure
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types

/**
 * Extension functions for mapping between Room entities and domain models.
 */

private val moshi = Moshi.Builder().build()
private val measuresListType = Types.newParameterizedType(List::class.java, ServingMeasureJson::class.java)
private val measuresAdapter = moshi.adapter<List<ServingMeasureJson>>(measuresListType)

/**
 * JSON representation of ServingMeasure for storage.
 */
data class ServingMeasureJson(
    val uri: String,
    val label: String,
    val weightGrams: Double
)

/**
 * Convert SearchedFood to RecentSearchEntity.
 */
fun SearchedFood.toRecentSearchEntity(): RecentSearchEntity {
    val measuresJsonList = measures.map {
        ServingMeasureJson(it.uri, it.label, it.weightGrams)
    }
    return RecentSearchEntity(
        foodId = foodId,
        name = name,
        brand = brand,
        category = category,
        imageUrl = imageUrl,
        caloriesPer100g = caloriesPer100g,
        proteinPer100g = proteinPer100g,
        carbsPer100g = carbsPer100g,
        fatPer100g = fatPer100g,
        fiberPer100g = fiberPer100g,
        sugarPer100g = sugarPer100g,
        sodiumPer100g = sodiumPer100g,
        measuresJson = measuresAdapter.toJson(measuresJsonList),
        timestamp = System.currentTimeMillis()
    )
}

/**
 * Convert SearchedFood to FavoriteFoodEntity.
 */
fun SearchedFood.toFavoriteFoodEntity(): FavoriteFoodEntity {
    val measuresJsonList = measures.map {
        ServingMeasureJson(it.uri, it.label, it.weightGrams)
    }
    return FavoriteFoodEntity(
        foodId = foodId,
        name = name,
        brand = brand,
        category = category,
        imageUrl = imageUrl,
        caloriesPer100g = caloriesPer100g,
        proteinPer100g = proteinPer100g,
        carbsPer100g = carbsPer100g,
        fatPer100g = fatPer100g,
        fiberPer100g = fiberPer100g,
        sugarPer100g = sugarPer100g,
        sodiumPer100g = sodiumPer100g,
        measuresJson = measuresAdapter.toJson(measuresJsonList),
        addedAt = System.currentTimeMillis()
    )
}

/**
 * Convert RecentSearchEntity to SearchedFood domain model.
 */
fun RecentSearchEntity.toDomainModel(): SearchedFood {
    val measuresJsonList = measuresAdapter.fromJson(measuresJson) ?: emptyList()
    val measures = measuresJsonList.map {
        ServingMeasure(it.uri, it.label, it.weightGrams)
    }
    return SearchedFood(
        foodId = foodId,
        name = name,
        brand = brand,
        category = category,
        imageUrl = imageUrl,
        caloriesPer100g = caloriesPer100g,
        proteinPer100g = proteinPer100g,
        carbsPer100g = carbsPer100g,
        fatPer100g = fatPer100g,
        fiberPer100g = fiberPer100g,
        sugarPer100g = sugarPer100g,
        sodiumPer100g = sodiumPer100g,
        measures = measures
    )
}

/**
 * Convert FavoriteFoodEntity to SearchedFood domain model.
 */
fun FavoriteFoodEntity.toDomainModel(): SearchedFood {
    val measuresJsonList = measuresAdapter.fromJson(measuresJson) ?: emptyList()
    val measures = measuresJsonList.map {
        ServingMeasure(it.uri, it.label, it.weightGrams)
    }
    return SearchedFood(
        foodId = foodId,
        name = name,
        brand = brand,
        category = category,
        imageUrl = imageUrl,
        caloriesPer100g = caloriesPer100g,
        proteinPer100g = proteinPer100g,
        carbsPer100g = carbsPer100g,
        fatPer100g = fatPer100g,
        fiberPer100g = fiberPer100g,
        sugarPer100g = sugarPer100g,
        sodiumPer100g = sodiumPer100g,
        measures = measures
    )
}

/**
 * Convert a list of RecentSearchEntity to domain models.
 */
fun List<RecentSearchEntity>.toRecentDomainModels(): List<SearchedFood> {
    return map { it.toDomainModel() }
}

/**
 * Convert a list of FavoriteFoodEntity to domain models.
 */
fun List<FavoriteFoodEntity>.toFavoriteDomainModels(): List<SearchedFood> {
    return map { it.toDomainModel() }
}

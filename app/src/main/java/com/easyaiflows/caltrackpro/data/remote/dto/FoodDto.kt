package com.easyaiflows.caltrackpro.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for food item data from Edamam API.
 * Represents a single food item in search results.
 */
@JsonClass(generateAdapter = true)
data class FoodDto(
    @Json(name = "foodId")
    val foodId: String,

    @Json(name = "label")
    val label: String,

    @Json(name = "knownAs")
    val knownAs: String? = null,

    @Json(name = "brand")
    val brand: String? = null,

    @Json(name = "category")
    val category: String? = null,

    @Json(name = "categoryLabel")
    val categoryLabel: String? = null,

    @Json(name = "nutrients")
    val nutrients: NutrientsDto,

    @Json(name = "image")
    val image: String? = null
)

/**
 * Represents a food hint from the search results.
 * Contains the food item and its available serving measures.
 */
@JsonClass(generateAdapter = true)
data class FoodHintDto(
    @Json(name = "food")
    val food: FoodDto,

    @Json(name = "measures")
    val measures: List<MeasureDto>
)

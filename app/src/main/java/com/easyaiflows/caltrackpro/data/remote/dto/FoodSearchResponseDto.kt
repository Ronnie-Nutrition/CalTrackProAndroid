package com.easyaiflows.caltrackpro.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for the Edamam Food Database API parser response.
 * Contains both parsed (exact matches) and hints (search results).
 */
@JsonClass(generateAdapter = true)
data class FoodSearchResponseDto(
    @Json(name = "text")
    val text: String? = null,

    @Json(name = "parsed")
    val parsed: List<ParsedFoodDto>? = null,

    @Json(name = "hints")
    val hints: List<FoodHintDto>? = null,

    @Json(name = "_links")
    val links: LinksDto? = null
)

/**
 * Represents a parsed food item (exact match from the query).
 */
@JsonClass(generateAdapter = true)
data class ParsedFoodDto(
    @Json(name = "food")
    val food: FoodDto
)

/**
 * Pagination links for the API response.
 */
@JsonClass(generateAdapter = true)
data class LinksDto(
    @Json(name = "next")
    val next: NextLinkDto? = null
)

/**
 * Next page link for pagination.
 */
@JsonClass(generateAdapter = true)
data class NextLinkDto(
    @Json(name = "href")
    val href: String,

    @Json(name = "title")
    val title: String? = null
)

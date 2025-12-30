package com.easyaiflows.caltrackpro.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for nutrient data from Edamam API.
 * Field names match Edamam's nutrient codes.
 *
 * All values are per 100g of the food item.
 */
@JsonClass(generateAdapter = true)
data class NutrientsDto(
    @Json(name = "ENERC_KCAL")
    val calories: Double? = null,

    @Json(name = "PROCNT")
    val protein: Double? = null,

    @Json(name = "FAT")
    val fat: Double? = null,

    @Json(name = "CHOCDF")
    val carbohydrates: Double? = null,

    @Json(name = "FIBTG")
    val fiber: Double? = null,

    @Json(name = "SUGAR")
    val sugar: Double? = null,

    @Json(name = "NA")
    val sodium: Double? = null
)

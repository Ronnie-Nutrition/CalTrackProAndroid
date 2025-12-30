package com.easyaiflows.caltrackpro.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for serving measure data from Edamam API.
 * Represents available serving sizes for a food item.
 */
@JsonClass(generateAdapter = true)
data class MeasureDto(
    @Json(name = "uri")
    val uri: String,

    @Json(name = "label")
    val label: String,

    @Json(name = "weight")
    val weight: Double
)

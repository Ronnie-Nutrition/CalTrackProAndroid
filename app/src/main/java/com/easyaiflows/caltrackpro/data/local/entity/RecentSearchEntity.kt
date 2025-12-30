package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing recent food searches.
 * Stores the food data from Edamam API for quick access without re-fetching.
 */
@Entity(tableName = "recent_searches")
data class RecentSearchEntity(
    @PrimaryKey
    val foodId: String,
    val name: String,
    val brand: String?,
    val category: String?,
    val imageUrl: String?,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val fiberPer100g: Double,
    val sugarPer100g: Double,
    val sodiumPer100g: Double,
    val measuresJson: String,
    val timestamp: Long
)

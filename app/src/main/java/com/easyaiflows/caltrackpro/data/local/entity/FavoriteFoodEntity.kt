package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing favorite foods.
 * Stores the full food data from Edamam API for offline access.
 */
@Entity(tableName = "favorite_foods")
data class FavoriteFoodEntity(
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
    val addedAt: Long
)

package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room entity for caching search results for offline access.
 * Each entry stores a query string mapped to a food item.
 */
@Entity(
    tableName = "cached_searches",
    indices = [Index(value = ["query"])]
)
data class CachedSearchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val query: String,
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
    val timestamp: Long = System.currentTimeMillis()
)

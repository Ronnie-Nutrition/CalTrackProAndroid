package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for caching scanned barcode results.
 * Uses barcode as primary key for efficient lookup by scanned code.
 */
@Entity(tableName = "scanned_barcodes")
data class ScannedBarcodeEntity(
    @PrimaryKey
    val barcode: String,
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
    val cachedAt: Long
)

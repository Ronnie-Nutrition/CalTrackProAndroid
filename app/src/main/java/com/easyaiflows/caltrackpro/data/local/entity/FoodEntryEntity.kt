package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_entries")
data class FoodEntryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val brand: String?,
    val barcode: String?,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double?,
    val sugar: Double?,
    val sodium: Double?,
    val servingSize: Double,
    val servingUnit: String,
    val quantity: Double,
    val mealType: String,
    val timestamp: Long,
    val imageData: ByteArray?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodEntryEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (brand != other.brand) return false
        if (barcode != other.barcode) return false
        if (calories != other.calories) return false
        if (protein != other.protein) return false
        if (carbs != other.carbs) return false
        if (fat != other.fat) return false
        if (fiber != other.fiber) return false
        if (sugar != other.sugar) return false
        if (sodium != other.sodium) return false
        if (servingSize != other.servingSize) return false
        if (servingUnit != other.servingUnit) return false
        if (quantity != other.quantity) return false
        if (mealType != other.mealType) return false
        if (timestamp != other.timestamp) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (brand?.hashCode() ?: 0)
        result = 31 * result + (barcode?.hashCode() ?: 0)
        result = 31 * result + calories.hashCode()
        result = 31 * result + protein.hashCode()
        result = 31 * result + carbs.hashCode()
        result = 31 * result + fat.hashCode()
        result = 31 * result + (fiber?.hashCode() ?: 0)
        result = 31 * result + (sugar?.hashCode() ?: 0)
        result = 31 * result + (sodium?.hashCode() ?: 0)
        result = 31 * result + servingSize.hashCode()
        result = 31 * result + servingUnit.hashCode()
        result = 31 * result + quantity.hashCode()
        result = 31 * result + mealType.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        return result
    }
}

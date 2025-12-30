package com.easyaiflows.caltrackpro.domain.model

import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class FoodEntry(
    val id: String,
    val name: String,
    val brand: String? = null,
    val barcode: String? = null,
    val calories: Double,
    val protein: Double,
    val carbs: Double,
    val fat: Double,
    val fiber: Double = 0.0,
    val sugar: Double = 0.0,
    val sodium: Double = 0.0,
    val servingSize: Double,
    val servingUnit: String,
    val quantity: Double = 1.0,
    val mealType: MealType,
    val timestamp: Long,
    val imageData: ByteArray? = null
) {
    /**
     * Total calories adjusted for quantity
     */
    val totalCalories: Double
        get() = calories * quantity

    /**
     * Total protein adjusted for quantity
     */
    val totalProtein: Double
        get() = protein * quantity

    /**
     * Total carbs adjusted for quantity
     */
    val totalCarbs: Double
        get() = carbs * quantity

    /**
     * Total fat adjusted for quantity
     */
    val totalFat: Double
        get() = fat * quantity

    /**
     * Total fiber adjusted for quantity
     */
    val totalFiber: Double
        get() = fiber * quantity

    /**
     * Total sugar adjusted for quantity
     */
    val totalSugar: Double
        get() = sugar * quantity

    /**
     * Total sodium adjusted for quantity
     */
    val totalSodium: Double
        get() = sodium * quantity

    /**
     * Formatted serving display (e.g., "100 g" or "2 x 100 g")
     */
    val servingDisplay: String
        get() = if (quantity == 1.0) {
            "${servingSize.formatAmount()} $servingUnit"
        } else {
            "${quantity.formatAmount()} x ${servingSize.formatAmount()} $servingUnit"
        }

    /**
     * LocalDate representation of the timestamp
     */
    val date: LocalDate
        get() = Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FoodEntry

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
        result = 31 * result + fiber.hashCode()
        result = 31 * result + sugar.hashCode()
        result = 31 * result + sodium.hashCode()
        result = 31 * result + servingSize.hashCode()
        result = 31 * result + servingUnit.hashCode()
        result = 31 * result + quantity.hashCode()
        result = 31 * result + mealType.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        return result
    }
}

/**
 * Format a number for display, removing unnecessary decimal places
 */
private fun Double.formatAmount(): String {
    return if (this == this.toLong().toDouble()) {
        this.toLong().toString()
    } else {
        String.format("%.1f", this)
    }
}

/**
 * Extension function to convert FoodEntryEntity to FoodEntry domain model
 */
fun FoodEntryEntity.toDomainModel(): FoodEntry {
    return FoodEntry(
        id = id,
        name = name,
        brand = brand,
        barcode = barcode,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        servingSize = servingSize,
        servingUnit = servingUnit,
        quantity = quantity,
        mealType = MealType.valueOf(mealType),
        timestamp = timestamp,
        imageData = imageData
    )
}

/**
 * Extension function to convert FoodEntry domain model to FoodEntryEntity
 */
fun FoodEntry.toEntity(): FoodEntryEntity {
    return FoodEntryEntity(
        id = id,
        name = name,
        brand = brand,
        barcode = barcode,
        calories = calories,
        protein = protein,
        carbs = carbs,
        fat = fat,
        fiber = fiber,
        sugar = sugar,
        sodium = sodium,
        servingSize = servingSize,
        servingUnit = servingUnit,
        quantity = quantity,
        mealType = mealType.name,
        timestamp = timestamp,
        imageData = imageData
    )
}

/**
 * Extension function to convert a list of entities to domain models
 */
fun List<FoodEntryEntity>.toDomainModels(): List<FoodEntry> {
    return map { it.toDomainModel() }
}

/**
 * Extension function to group entries by MealType
 */
fun List<FoodEntry>.groupByMealType(): Map<MealType, List<FoodEntry>> {
    return groupBy { it.mealType }
        .toSortedMap(compareBy { it.ordinal })
}

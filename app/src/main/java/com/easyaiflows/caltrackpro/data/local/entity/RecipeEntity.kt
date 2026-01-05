package com.easyaiflows.caltrackpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room entity for storing recipes in the database.
 * Ingredients and instructions are stored as JSON strings.
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val ingredients: String,  // JSON array of RecipeIngredient
    val instructions: String, // JSON array of String
    val servings: Int,
    val cookingTimeMinutes: Int,
    val difficulty: String,   // "EASY", "MEDIUM", "HARD"
    val category: String,     // "BREAKFAST", "LUNCH", etc.
    val imageData: ByteArray?,
    val createdAt: Long,
    val caloriesPerServing: Double,
    val proteinPerServing: Double,
    val carbsPerServing: Double,
    val fatPerServing: Double
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipeEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (servings != other.servings) return false
        if (cookingTimeMinutes != other.cookingTimeMinutes) return false
        if (difficulty != other.difficulty) return false
        if (category != other.category) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false
        if (createdAt != other.createdAt) return false
        if (caloriesPerServing != other.caloriesPerServing) return false
        if (proteinPerServing != other.proteinPerServing) return false
        if (carbsPerServing != other.carbsPerServing) return false
        if (fatPerServing != other.fatPerServing) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + servings
        result = 31 * result + cookingTimeMinutes
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + caloriesPerServing.hashCode()
        result = 31 * result + proteinPerServing.hashCode()
        result = 31 * result + carbsPerServing.hashCode()
        result = 31 * result + fatPerServing.hashCode()
        return result
    }
}

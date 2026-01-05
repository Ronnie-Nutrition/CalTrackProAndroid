package com.easyaiflows.caltrackpro.domain.model

import java.util.UUID

/**
 * Represents a recipe with ingredients, instructions, and nutrition information.
 */
data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val ingredients: List<RecipeIngredient>,
    val instructions: List<String> = emptyList(),
    val servings: Int,
    val cookingTimeMinutes: Int,
    val difficulty: RecipeDifficulty,
    val category: RecipeCategory,
    val imageData: ByteArray? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    // Task 1.6: Computed properties for total nutrition (summing all ingredients)

    /**
     * Total calories from all ingredients in the recipe.
     */
    val totalCalories: Double
        get() = ingredients.sumOf { it.calories }

    /**
     * Total protein from all ingredients in the recipe.
     */
    val totalProtein: Double
        get() = ingredients.sumOf { it.protein }

    /**
     * Total carbs from all ingredients in the recipe.
     */
    val totalCarbs: Double
        get() = ingredients.sumOf { it.carbs }

    /**
     * Total fat from all ingredients in the recipe.
     */
    val totalFat: Double
        get() = ingredients.sumOf { it.fat }

    // Task 1.7: Computed properties for per-serving nutrition

    /**
     * Calories per serving.
     */
    val caloriesPerServing: Double
        get() = if (servings > 0) totalCalories / servings else 0.0

    /**
     * Protein per serving.
     */
    val proteinPerServing: Double
        get() = if (servings > 0) totalProtein / servings else 0.0

    /**
     * Carbs per serving.
     */
    val carbsPerServing: Double
        get() = if (servings > 0) totalCarbs / servings else 0.0

    /**
     * Fat per serving.
     */
    val fatPerServing: Double
        get() = if (servings > 0) totalFat / servings else 0.0

    /**
     * Nutrition info per serving as a convenience data class.
     */
    data class NutritionInfo(
        val calories: Double,
        val protein: Double,
        val carbs: Double,
        val fat: Double
    )

    /**
     * Get nutrition per serving as a NutritionInfo object.
     */
    val nutritionPerServing: NutritionInfo
        get() = NutritionInfo(
            calories = caloriesPerServing,
            protein = proteinPerServing,
            carbs = carbsPerServing,
            fat = fatPerServing
        )

    // ByteArray requires custom equals/hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Recipe

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
        return result
    }
}

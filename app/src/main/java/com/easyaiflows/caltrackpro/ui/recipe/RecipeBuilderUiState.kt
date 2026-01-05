package com.easyaiflows.caltrackpro.ui.recipe

import com.easyaiflows.caltrackpro.domain.model.RecipeCategory
import com.easyaiflows.caltrackpro.domain.model.RecipeDifficulty
import com.easyaiflows.caltrackpro.domain.model.RecipeIngredient

/**
 * UI state for the Recipe Builder screen.
 */
data class RecipeBuilderUiState(
    // Recipe identification (null for create, non-null for edit)
    val recipeId: String? = null,

    // Basic info
    val name: String = "",
    val description: String = "",

    // Recipe settings
    val servings: Int = 4,
    val cookingTimeMinutes: Int = 30,
    val difficulty: RecipeDifficulty = RecipeDifficulty.MEDIUM,
    val category: RecipeCategory = RecipeCategory.MAIN,

    // Ingredients and instructions
    val ingredients: List<RecipeIngredient> = emptyList(),
    val instructions: List<String> = listOf(""),

    // Image
    val imageData: ByteArray? = null,

    // UI state
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val showIngredientSearch: Boolean = false,
    val editingIngredientIndex: Int? = null,

    // Validation errors
    val nameError: String? = null,
    val ingredientsError: String? = null
) {
    /**
     * Whether this is editing an existing recipe.
     */
    val isEditMode: Boolean
        get() = recipeId != null

    /**
     * Total calories from all ingredients.
     */
    val totalCalories: Double
        get() = ingredients.sumOf { it.calories }

    /**
     * Total protein from all ingredients.
     */
    val totalProtein: Double
        get() = ingredients.sumOf { it.protein }

    /**
     * Total carbs from all ingredients.
     */
    val totalCarbs: Double
        get() = ingredients.sumOf { it.carbs }

    /**
     * Total fat from all ingredients.
     */
    val totalFat: Double
        get() = ingredients.sumOf { it.fat }

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
     * Whether the form is valid for saving.
     */
    val canSave: Boolean
        get() = name.isNotBlank() && ingredients.isNotEmpty() && !isSaving

    /**
     * Whether nutrition preview should be shown.
     */
    val showNutritionPreview: Boolean
        get() = ingredients.isNotEmpty()

    // ByteArray requires custom equals/hashCode
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RecipeBuilderUiState

        if (recipeId != other.recipeId) return false
        if (name != other.name) return false
        if (description != other.description) return false
        if (servings != other.servings) return false
        if (cookingTimeMinutes != other.cookingTimeMinutes) return false
        if (difficulty != other.difficulty) return false
        if (category != other.category) return false
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (imageData != null) {
            if (other.imageData == null) return false
            if (!imageData.contentEquals(other.imageData)) return false
        } else if (other.imageData != null) return false
        if (isSaving != other.isSaving) return false
        if (isLoading != other.isLoading) return false
        if (showIngredientSearch != other.showIngredientSearch) return false
        if (editingIngredientIndex != other.editingIngredientIndex) return false
        if (nameError != other.nameError) return false
        if (ingredientsError != other.ingredientsError) return false

        return true
    }

    override fun hashCode(): Int {
        var result = recipeId?.hashCode() ?: 0
        result = 31 * result + name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + servings
        result = 31 * result + cookingTimeMinutes
        result = 31 * result + difficulty.hashCode()
        result = 31 * result + category.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + (imageData?.contentHashCode() ?: 0)
        result = 31 * result + isSaving.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + showIngredientSearch.hashCode()
        result = 31 * result + (editingIngredientIndex ?: 0)
        result = 31 * result + (nameError?.hashCode() ?: 0)
        result = 31 * result + (ingredientsError?.hashCode() ?: 0)
        return result
    }
}

package com.easyaiflows.caltrackpro.ui.recipe

import com.easyaiflows.caltrackpro.domain.model.Recipe
import com.easyaiflows.caltrackpro.domain.model.RecipeCategory

/**
 * Sort order options for the recipe library.
 */
enum class RecipeSortOrder(val displayName: String) {
    NEWEST("Newest"),
    OLDEST("Oldest"),
    NAME_AZ("Name A-Z"),
    NAME_ZA("Name Z-A"),
    COOK_TIME("Cook Time"),
    DIFFICULTY("Difficulty")
}

/**
 * UI state for the Recipe Library screen.
 */
data class RecipeLibraryUiState(
    val recipes: List<Recipe> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: RecipeCategory? = null,
    val sortOrder: RecipeSortOrder = RecipeSortOrder.NEWEST,
    val isLoading: Boolean = true
) {
    /**
     * Filtered and sorted recipes based on current search, category, and sort settings.
     */
    val filteredRecipes: List<Recipe>
        get() {
            var result = recipes

            // Filter by search query
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                result = result.filter { recipe ->
                    recipe.name.lowercase().contains(query) ||
                    recipe.description.lowercase().contains(query)
                }
            }

            // Filter by category
            if (selectedCategory != null) {
                result = result.filter { it.category == selectedCategory }
            }

            // Sort
            result = when (sortOrder) {
                RecipeSortOrder.NEWEST -> result.sortedByDescending { it.createdAt }
                RecipeSortOrder.OLDEST -> result.sortedBy { it.createdAt }
                RecipeSortOrder.NAME_AZ -> result.sortedBy { it.name.lowercase() }
                RecipeSortOrder.NAME_ZA -> result.sortedByDescending { it.name.lowercase() }
                RecipeSortOrder.COOK_TIME -> result.sortedBy { it.cookingTimeMinutes }
                RecipeSortOrder.DIFFICULTY -> result.sortedBy { it.difficulty.ordinal }
            }

            return result
        }
}

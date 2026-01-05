package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for recipe operations.
 * Uses domain Recipe model for better abstraction.
 */
interface RecipeRepository {

    /**
     * Get all recipes as a Flow, ordered by creation date descending.
     */
    fun getAllRecipes(): Flow<List<Recipe>>

    /**
     * Get a recipe by its ID as a Flow.
     */
    fun getRecipeById(id: String): Flow<Recipe?>

    /**
     * Search recipes by name or description.
     */
    fun searchRecipes(query: String): Flow<List<Recipe>>

    /**
     * Get recipes filtered by category.
     */
    fun getRecipesByCategory(category: String): Flow<List<Recipe>>

    /**
     * Get the total count of recipes.
     */
    fun getRecipeCount(): Flow<Int>

    /**
     * Insert a new recipe.
     */
    suspend fun insertRecipe(recipe: Recipe)

    /**
     * Update an existing recipe.
     */
    suspend fun updateRecipe(recipe: Recipe)

    /**
     * Delete a recipe.
     */
    suspend fun deleteRecipe(recipe: Recipe)

    /**
     * Delete a recipe by its ID.
     */
    suspend fun deleteRecipeById(id: String)
}

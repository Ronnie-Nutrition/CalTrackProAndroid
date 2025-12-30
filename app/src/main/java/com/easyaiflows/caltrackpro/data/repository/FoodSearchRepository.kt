package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for food search operations.
 * Provides abstraction over the Edamam API for searching foods.
 */
interface FoodSearchRepository {

    /**
     * Search for foods by query string.
     *
     * @param query The search query (e.g., "chicken breast", "apple")
     * @return Result containing list of SearchedFood or error
     */
    suspend fun searchFoods(query: String): Result<List<SearchedFood>>

    /**
     * Get a specific food by its ID.
     * Useful for retrieving food details from cached/saved items.
     *
     * @param foodId The Edamam food ID
     * @return Result containing SearchedFood or error
     */
    suspend fun getFoodById(foodId: String): Result<SearchedFood?>

    // Recent Searches

    /**
     * Get recent food searches.
     */
    fun getRecentSearches(): Flow<List<SearchedFood>>

    /**
     * Add a food to recent searches.
     */
    suspend fun addToRecentSearches(food: SearchedFood)

    /**
     * Clear all recent searches.
     */
    suspend fun clearRecentSearches()

    // Favorites

    /**
     * Get all favorite foods.
     */
    fun getFavorites(): Flow<List<SearchedFood>>

    /**
     * Add a food to favorites.
     */
    suspend fun addToFavorites(food: SearchedFood)

    /**
     * Remove a food from favorites.
     */
    suspend fun removeFromFavorites(foodId: String)

    /**
     * Check if a food is in favorites.
     */
    suspend fun isFavorite(foodId: String): Boolean

    /**
     * Check if a food is in favorites (reactive).
     */
    fun isFavoriteFlow(foodId: String): Flow<Boolean>

    /**
     * Toggle favorite status for a food.
     */
    suspend fun toggleFavorite(food: SearchedFood)
}

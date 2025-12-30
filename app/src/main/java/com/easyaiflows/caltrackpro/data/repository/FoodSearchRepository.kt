package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.domain.model.SearchedFood

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
}

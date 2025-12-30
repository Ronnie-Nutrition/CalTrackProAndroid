package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.remote.EdamamApiService
import com.easyaiflows.caltrackpro.data.remote.dto.toDomainModels
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FoodSearchRepository using Edamam API.
 */
@Singleton
class FoodSearchRepositoryImpl @Inject constructor(
    private val apiService: EdamamApiService
) : FoodSearchRepository {

    // In-memory cache for recent search results (for getFoodById lookups)
    private val searchCache = mutableMapOf<String, SearchedFood>()

    override suspend fun searchFoods(query: String): Result<List<SearchedFood>> {
        return try {
            if (query.isBlank()) {
                return Result.success(emptyList())
            }

            val response = apiService.searchFoods(query)
            val foods = response.toDomainModels()

            // Cache results for later lookup by ID
            foods.forEach { food ->
                searchCache[food.foodId] = food
            }

            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFoodById(foodId: String): Result<SearchedFood?> {
        return try {
            // First check cache
            val cached = searchCache[foodId]
            if (cached != null) {
                return Result.success(cached)
            }

            // If not in cache, search by foodId
            // Note: Edamam doesn't have a direct "get by ID" endpoint,
            // so we search by the food label if needed
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Clear the search cache.
     */
    fun clearCache() {
        searchCache.clear()
    }
}

package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.dao.FavoriteFoodDao
import com.easyaiflows.caltrackpro.data.local.dao.RecentSearchDao
import com.easyaiflows.caltrackpro.data.local.entity.toDomainModel
import com.easyaiflows.caltrackpro.data.local.entity.toFavoriteDomainModels
import com.easyaiflows.caltrackpro.data.local.entity.toFavoriteFoodEntity
import com.easyaiflows.caltrackpro.data.local.entity.toRecentDomainModels
import com.easyaiflows.caltrackpro.data.local.entity.toRecentSearchEntity
import com.easyaiflows.caltrackpro.data.remote.EdamamApiService
import com.easyaiflows.caltrackpro.data.remote.dto.toDomainModels
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of FoodSearchRepository using Edamam API.
 */
@Singleton
class FoodSearchRepositoryImpl @Inject constructor(
    private val apiService: EdamamApiService,
    private val recentSearchDao: RecentSearchDao,
    private val favoriteFoodDao: FavoriteFoodDao
) : FoodSearchRepository {

    companion object {
        private const val MAX_RECENT_SEARCHES = 20
    }

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
            // First check in-memory cache
            val cached = searchCache[foodId]
            if (cached != null) {
                return Result.success(cached)
            }

            // Check favorites
            val favorite = favoriteFoodDao.getById(foodId)
            if (favorite != null) {
                val food = favorite.toDomainModel()
                searchCache[foodId] = food
                return Result.success(food)
            }

            // If not in cache or favorites, return null
            // Note: Edamam doesn't have a direct "get by ID" endpoint
            Result.success(null)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Recent Searches

    override fun getRecentSearches(): Flow<List<SearchedFood>> {
        return recentSearchDao.getRecent(MAX_RECENT_SEARCHES).map { entities ->
            entities.toRecentDomainModels()
        }
    }

    override suspend fun addToRecentSearches(food: SearchedFood) {
        // Add to cache
        searchCache[food.foodId] = food

        // Add to database
        recentSearchDao.insert(food.toRecentSearchEntity())

        // Cleanup old entries
        recentSearchDao.deleteOldest(MAX_RECENT_SEARCHES)
    }

    override suspend fun clearRecentSearches() {
        recentSearchDao.deleteAll()
    }

    // Favorites

    override fun getFavorites(): Flow<List<SearchedFood>> {
        return favoriteFoodDao.getAll().map { entities ->
            entities.toFavoriteDomainModels()
        }
    }

    override suspend fun addToFavorites(food: SearchedFood) {
        // Add to cache
        searchCache[food.foodId] = food

        // Add to database
        favoriteFoodDao.insert(food.toFavoriteFoodEntity())
    }

    override suspend fun removeFromFavorites(foodId: String) {
        favoriteFoodDao.delete(foodId)
    }

    override suspend fun isFavorite(foodId: String): Boolean {
        return favoriteFoodDao.isFavorite(foodId)
    }

    override fun isFavoriteFlow(foodId: String): Flow<Boolean> {
        return favoriteFoodDao.isFavoriteFlow(foodId)
    }

    override suspend fun toggleFavorite(food: SearchedFood) {
        if (isFavorite(food.foodId)) {
            removeFromFavorites(food.foodId)
        } else {
            addToFavorites(food)
        }
    }

    /**
     * Clear the in-memory search cache.
     */
    fun clearCache() {
        searchCache.clear()
    }
}

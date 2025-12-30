package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easyaiflows.caltrackpro.data.local.entity.FavoriteFoodEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for favorite foods.
 */
@Dao
interface FavoriteFoodDao {

    /**
     * Insert or update a favorite food.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteFood: FavoriteFoodEntity)

    /**
     * Get all favorite foods ordered by most recently added first.
     */
    @Query("SELECT * FROM favorite_foods ORDER BY addedAt DESC")
    fun getAll(): Flow<List<FavoriteFoodEntity>>

    /**
     * Delete a specific favorite food by food ID.
     */
    @Query("DELETE FROM favorite_foods WHERE foodId = :foodId")
    suspend fun delete(foodId: String)

    /**
     * Delete all favorite foods.
     */
    @Query("DELETE FROM favorite_foods")
    suspend fun deleteAll()

    /**
     * Check if a food is favorited.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_foods WHERE foodId = :foodId)")
    suspend fun isFavorite(foodId: String): Boolean

    /**
     * Check if a food is favorited (as Flow for reactive UI).
     */
    @Query("SELECT EXISTS(SELECT 1 FROM favorite_foods WHERE foodId = :foodId)")
    fun isFavoriteFlow(foodId: String): Flow<Boolean>

    /**
     * Get a specific favorite food by ID.
     */
    @Query("SELECT * FROM favorite_foods WHERE foodId = :foodId")
    suspend fun getById(foodId: String): FavoriteFoodEntity?

    /**
     * Get the count of favorite foods.
     */
    @Query("SELECT COUNT(*) FROM favorite_foods")
    suspend fun getCount(): Int
}

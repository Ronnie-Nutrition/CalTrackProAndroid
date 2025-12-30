package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easyaiflows.caltrackpro.data.local.entity.RecentSearchEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for recent food searches.
 */
@Dao
interface RecentSearchDao {

    /**
     * Insert or update a recent search.
     * If the food already exists, update its timestamp.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recentSearch: RecentSearchEntity)

    /**
     * Get all recent searches ordered by most recent first.
     */
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC")
    fun getAll(): Flow<List<RecentSearchEntity>>

    /**
     * Get a limited number of recent searches.
     */
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<RecentSearchEntity>>

    /**
     * Delete a specific recent search by food ID.
     */
    @Query("DELETE FROM recent_searches WHERE foodId = :foodId")
    suspend fun delete(foodId: String)

    /**
     * Delete all recent searches.
     */
    @Query("DELETE FROM recent_searches")
    suspend fun deleteAll()

    /**
     * Delete oldest searches, keeping only the most recent ones.
     * Used for automatic cleanup.
     */
    @Query("""
        DELETE FROM recent_searches
        WHERE foodId NOT IN (
            SELECT foodId FROM recent_searches
            ORDER BY timestamp DESC
            LIMIT :keepCount
        )
    """)
    suspend fun deleteOldest(keepCount: Int)

    /**
     * Get the count of recent searches.
     */
    @Query("SELECT COUNT(*) FROM recent_searches")
    suspend fun getCount(): Int
}

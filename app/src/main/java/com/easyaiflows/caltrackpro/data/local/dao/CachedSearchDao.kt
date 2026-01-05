package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.easyaiflows.caltrackpro.data.local.entity.CachedSearchEntity

/**
 * DAO for cached search results operations.
 */
@Dao
interface CachedSearchDao {

    /**
     * Insert cached search results.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cachedSearches: List<CachedSearchEntity>)

    /**
     * Get cached results for a query (case-insensitive partial match).
     */
    @Query("SELECT * FROM cached_searches WHERE LOWER(query) = LOWER(:query) ORDER BY id ASC")
    suspend fun getByQuery(query: String): List<CachedSearchEntity>

    /**
     * Delete cached results for a query.
     */
    @Query("DELETE FROM cached_searches WHERE LOWER(query) = LOWER(:query)")
    suspend fun deleteByQuery(query: String)

    /**
     * Delete all cached searches.
     */
    @Query("DELETE FROM cached_searches")
    suspend fun deleteAll()

    /**
     * Delete old cached searches (older than given timestamp).
     */
    @Query("DELETE FROM cached_searches WHERE timestamp < :timestamp")
    suspend fun deleteOlderThan(timestamp: Long)

    /**
     * Get count of cached entries.
     */
    @Query("SELECT COUNT(*) FROM cached_searches")
    suspend fun getCount(): Int
}

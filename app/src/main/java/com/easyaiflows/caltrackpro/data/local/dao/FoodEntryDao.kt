package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: FoodEntryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<FoodEntryEntity>)

    @Update
    suspend fun update(entry: FoodEntryEntity)

    @Delete
    suspend fun delete(entry: FoodEntryEntity)

    @Query("DELETE FROM food_entries WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM food_entries WHERE id = :id")
    suspend fun getById(id: String): FoodEntryEntity?

    @Query("SELECT * FROM food_entries WHERE id = :id")
    fun getByIdFlow(id: String): Flow<FoodEntryEntity?>

    @Query("SELECT * FROM food_entries WHERE timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp ASC")
    fun getEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntryEntity>>

    @Query("SELECT * FROM food_entries WHERE timestamp >= :startTime AND timestamp < :endTime ORDER BY timestamp ASC")
    fun getEntriesInRange(startTime: Long, endTime: Long): Flow<List<FoodEntryEntity>>

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FoodEntryEntity>>

    @Query("SELECT * FROM food_entries WHERE mealType = :mealType AND timestamp >= :startOfDay AND timestamp < :endOfDay ORDER BY timestamp ASC")
    fun getEntriesByMealType(mealType: String, startOfDay: Long, endOfDay: Long): Flow<List<FoodEntryEntity>>

    @Query("DELETE FROM food_entries")
    suspend fun deleteAll()
}

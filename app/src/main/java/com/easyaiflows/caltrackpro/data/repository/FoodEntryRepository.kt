package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.Flow

interface FoodEntryRepository {

    suspend fun insert(entry: FoodEntryEntity)

    suspend fun insertAll(entries: List<FoodEntryEntity>)

    suspend fun update(entry: FoodEntryEntity)

    suspend fun delete(entry: FoodEntryEntity)

    suspend fun deleteById(id: String)

    suspend fun getById(id: String): FoodEntryEntity?

    fun getByIdFlow(id: String): Flow<FoodEntryEntity?>

    fun getEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntryEntity>>

    fun getEntriesInRange(startTime: Long, endTime: Long): Flow<List<FoodEntryEntity>>

    fun getAllEntries(): Flow<List<FoodEntryEntity>>

    fun getEntriesByMealType(mealType: String, startOfDay: Long, endOfDay: Long): Flow<List<FoodEntryEntity>>

    suspend fun deleteAll()
}

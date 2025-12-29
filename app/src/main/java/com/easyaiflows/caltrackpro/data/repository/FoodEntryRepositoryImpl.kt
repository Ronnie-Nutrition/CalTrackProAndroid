package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.dao.FoodEntryDao
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodEntryRepositoryImpl @Inject constructor(
    private val foodEntryDao: FoodEntryDao
) : FoodEntryRepository {

    override suspend fun insert(entry: FoodEntryEntity) {
        foodEntryDao.insert(entry)
    }

    override suspend fun insertAll(entries: List<FoodEntryEntity>) {
        foodEntryDao.insertAll(entries)
    }

    override suspend fun update(entry: FoodEntryEntity) {
        foodEntryDao.update(entry)
    }

    override suspend fun delete(entry: FoodEntryEntity) {
        foodEntryDao.delete(entry)
    }

    override suspend fun deleteById(id: String) {
        foodEntryDao.deleteById(id)
    }

    override suspend fun getById(id: String): FoodEntryEntity? {
        return foodEntryDao.getById(id)
    }

    override fun getByIdFlow(id: String): Flow<FoodEntryEntity?> {
        return foodEntryDao.getByIdFlow(id)
    }

    override fun getEntriesForDay(startOfDay: Long, endOfDay: Long): Flow<List<FoodEntryEntity>> {
        return foodEntryDao.getEntriesForDay(startOfDay, endOfDay)
    }

    override fun getEntriesInRange(startTime: Long, endTime: Long): Flow<List<FoodEntryEntity>> {
        return foodEntryDao.getEntriesInRange(startTime, endTime)
    }

    override fun getAllEntries(): Flow<List<FoodEntryEntity>> {
        return foodEntryDao.getAllEntries()
    }

    override fun getEntriesByMealType(
        mealType: String,
        startOfDay: Long,
        endOfDay: Long
    ): Flow<List<FoodEntryEntity>> {
        return foodEntryDao.getEntriesByMealType(mealType, startOfDay, endOfDay)
    }

    override suspend fun deleteAll() {
        foodEntryDao.deleteAll()
    }
}

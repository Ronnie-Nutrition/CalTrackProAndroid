package com.easyaiflows.caltrackpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.easyaiflows.caltrackpro.data.local.converter.Converters
import com.easyaiflows.caltrackpro.data.local.dao.FoodEntryDao
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity

@Database(
    entities = [FoodEntryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CalTrackDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao

    companion object {
        const val DATABASE_NAME = "caltrack_database"
    }
}

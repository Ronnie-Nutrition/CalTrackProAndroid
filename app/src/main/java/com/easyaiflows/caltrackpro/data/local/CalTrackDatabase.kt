package com.easyaiflows.caltrackpro.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.easyaiflows.caltrackpro.data.local.converter.Converters
import com.easyaiflows.caltrackpro.data.local.converter.RecipeConverters
import com.easyaiflows.caltrackpro.data.local.dao.CachedSearchDao
import com.easyaiflows.caltrackpro.data.local.dao.FavoriteFoodDao
import com.easyaiflows.caltrackpro.data.local.dao.FoodEntryDao
import com.easyaiflows.caltrackpro.data.local.dao.RecentSearchDao
import com.easyaiflows.caltrackpro.data.local.dao.RecipeDao
import com.easyaiflows.caltrackpro.data.local.dao.ScannedBarcodeDao
import com.easyaiflows.caltrackpro.data.local.entity.CachedSearchEntity
import com.easyaiflows.caltrackpro.data.local.entity.FavoriteFoodEntity
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import com.easyaiflows.caltrackpro.data.local.entity.RecentSearchEntity
import com.easyaiflows.caltrackpro.data.local.entity.RecipeEntity
import com.easyaiflows.caltrackpro.data.local.entity.ScannedBarcodeEntity

@Database(
    entities = [
        FoodEntryEntity::class,
        RecentSearchEntity::class,
        FavoriteFoodEntity::class,
        CachedSearchEntity::class,
        ScannedBarcodeEntity::class,
        RecipeEntity::class
    ],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class, RecipeConverters::class)
abstract class CalTrackDatabase : RoomDatabase() {
    abstract fun foodEntryDao(): FoodEntryDao
    abstract fun recentSearchDao(): RecentSearchDao
    abstract fun favoriteFoodDao(): FavoriteFoodDao
    abstract fun cachedSearchDao(): CachedSearchDao
    abstract fun scannedBarcodeDao(): ScannedBarcodeDao
    abstract fun recipeDao(): RecipeDao

    companion object {
        const val DATABASE_NAME = "caltrack_database"
    }
}

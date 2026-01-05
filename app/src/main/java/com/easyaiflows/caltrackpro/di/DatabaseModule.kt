package com.easyaiflows.caltrackpro.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.easyaiflows.caltrackpro.data.local.CalTrackDatabase
import com.easyaiflows.caltrackpro.data.local.dao.CachedSearchDao
import com.easyaiflows.caltrackpro.data.local.dao.FavoriteFoodDao
import com.easyaiflows.caltrackpro.data.local.dao.FoodEntryDao
import com.easyaiflows.caltrackpro.data.local.dao.RecentSearchDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create recent_searches table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS recent_searches (
                    foodId TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    brand TEXT,
                    category TEXT,
                    imageUrl TEXT,
                    caloriesPer100g REAL NOT NULL,
                    proteinPer100g REAL NOT NULL,
                    carbsPer100g REAL NOT NULL,
                    fatPer100g REAL NOT NULL,
                    fiberPer100g REAL NOT NULL,
                    sugarPer100g REAL NOT NULL,
                    sodiumPer100g REAL NOT NULL,
                    measuresJson TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """)

            // Create favorite_foods table
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS favorite_foods (
                    foodId TEXT PRIMARY KEY NOT NULL,
                    name TEXT NOT NULL,
                    brand TEXT,
                    category TEXT,
                    imageUrl TEXT,
                    caloriesPer100g REAL NOT NULL,
                    proteinPer100g REAL NOT NULL,
                    carbsPer100g REAL NOT NULL,
                    fatPer100g REAL NOT NULL,
                    fiberPer100g REAL NOT NULL,
                    sugarPer100g REAL NOT NULL,
                    sodiumPer100g REAL NOT NULL,
                    measuresJson TEXT NOT NULL,
                    addedAt INTEGER NOT NULL
                )
            """)
        }
    }

    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Create cached_searches table for offline search results
            db.execSQL("""
                CREATE TABLE IF NOT EXISTS cached_searches (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    query TEXT NOT NULL,
                    foodId TEXT NOT NULL,
                    name TEXT NOT NULL,
                    brand TEXT,
                    category TEXT,
                    imageUrl TEXT,
                    caloriesPer100g REAL NOT NULL,
                    proteinPer100g REAL NOT NULL,
                    carbsPer100g REAL NOT NULL,
                    fatPer100g REAL NOT NULL,
                    fiberPer100g REAL NOT NULL,
                    sugarPer100g REAL NOT NULL,
                    sodiumPer100g REAL NOT NULL,
                    measuresJson TEXT NOT NULL,
                    timestamp INTEGER NOT NULL
                )
            """)
            // Create index on query column for faster lookups
            db.execSQL("CREATE INDEX IF NOT EXISTS index_cached_searches_query ON cached_searches(query)")
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CalTrackDatabase {
        return Room.databaseBuilder(
            context,
            CalTrackDatabase::class.java,
            CalTrackDatabase.DATABASE_NAME
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(database: CalTrackDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }

    @Provides
    @Singleton
    fun provideRecentSearchDao(database: CalTrackDatabase): RecentSearchDao {
        return database.recentSearchDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteFoodDao(database: CalTrackDatabase): FavoriteFoodDao {
        return database.favoriteFoodDao()
    }

    @Provides
    @Singleton
    fun provideCachedSearchDao(database: CalTrackDatabase): CachedSearchDao {
        return database.cachedSearchDao()
    }
}

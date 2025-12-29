package com.easyaiflows.caltrackpro.di

import android.content.Context
import androidx.room.Room
import com.easyaiflows.caltrackpro.data.local.CalTrackDatabase
import com.easyaiflows.caltrackpro.data.local.dao.FoodEntryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CalTrackDatabase {
        return Room.databaseBuilder(
            context,
            CalTrackDatabase::class.java,
            CalTrackDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideFoodEntryDao(database: CalTrackDatabase): FoodEntryDao {
        return database.foodEntryDao()
    }
}

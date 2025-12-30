package com.easyaiflows.caltrackpro.di

import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepositoryImpl
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFoodEntryRepository(
        foodEntryRepositoryImpl: FoodEntryRepositoryImpl
    ): FoodEntryRepository

    @Binds
    @Singleton
    abstract fun bindFoodSearchRepository(
        foodSearchRepositoryImpl: FoodSearchRepositoryImpl
    ): FoodSearchRepository
}

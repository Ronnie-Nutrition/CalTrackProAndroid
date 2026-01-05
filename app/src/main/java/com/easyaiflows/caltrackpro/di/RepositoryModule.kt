package com.easyaiflows.caltrackpro.di

import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepositoryImpl
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepositoryImpl
import com.easyaiflows.caltrackpro.data.repository.RecipeRepository
import com.easyaiflows.caltrackpro.data.repository.RecipeRepositoryImpl
import com.easyaiflows.caltrackpro.data.repository.UserProfileRepository
import com.easyaiflows.caltrackpro.data.repository.UserProfileRepositoryImpl
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

    @Binds
    @Singleton
    abstract fun bindUserProfileRepository(
        userProfileRepositoryImpl: UserProfileRepositoryImpl
    ): UserProfileRepository

    @Binds
    @Singleton
    abstract fun bindRecipeRepository(
        recipeRepositoryImpl: RecipeRepositoryImpl
    ): RecipeRepository
}

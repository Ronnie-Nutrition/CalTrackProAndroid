package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.converter.RecipeConverters
import com.easyaiflows.caltrackpro.data.local.dao.RecipeDao
import com.easyaiflows.caltrackpro.data.local.entity.RecipeEntity
import com.easyaiflows.caltrackpro.domain.model.Recipe
import com.easyaiflows.caltrackpro.domain.model.RecipeCategory
import com.easyaiflows.caltrackpro.domain.model.RecipeDifficulty
import com.easyaiflows.caltrackpro.domain.model.RecipeIngredient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of RecipeRepository using Room database.
 */
class RecipeRepositoryImpl @Inject constructor(
    private val recipeDao: RecipeDao
) : RecipeRepository {

    private val converters = RecipeConverters()

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecipeById(id: String): Flow<Recipe?> {
        return recipeDao.getRecipeById(id).map { entity ->
            entity?.toDomain()
        }
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecipesByCategory(category: String): Flow<List<Recipe>> {
        return recipeDao.getRecipesByCategory(category).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRecipeCount(): Flow<Int> {
        return recipeDao.getRecipeCount()
    }

    override suspend fun insertRecipe(recipe: Recipe) {
        recipeDao.insert(recipe.toEntity())
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeDao.update(recipe.toEntity())
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeDao.delete(recipe.toEntity())
    }

    override suspend fun deleteRecipeById(id: String) {
        recipeDao.deleteById(id)
    }

    // Entity to Domain mapping
    private fun RecipeEntity.toDomain(): Recipe {
        val ingredientsList = converters.toIngredientList(ingredients)
        val instructionsList = converters.toStringList(instructions)

        return Recipe(
            id = id,
            name = name,
            description = description,
            ingredients = ingredientsList,
            instructions = instructionsList,
            servings = servings,
            cookingTimeMinutes = cookingTimeMinutes,
            difficulty = RecipeDifficulty.fromString(difficulty),
            category = RecipeCategory.fromString(category),
            imageData = imageData,
            createdAt = createdAt
        )
    }

    // Domain to Entity mapping
    private fun Recipe.toEntity(): RecipeEntity {
        return RecipeEntity(
            id = id,
            name = name,
            description = description,
            ingredients = converters.fromIngredientList(ingredients),
            instructions = converters.fromStringList(instructions),
            servings = servings,
            cookingTimeMinutes = cookingTimeMinutes,
            difficulty = difficulty.name,
            category = category.name,
            imageData = imageData,
            createdAt = createdAt,
            caloriesPerServing = caloriesPerServing,
            proteinPerServing = proteinPerServing,
            carbsPerServing = carbsPerServing,
            fatPerServing = fatPerServing
        )
    }
}

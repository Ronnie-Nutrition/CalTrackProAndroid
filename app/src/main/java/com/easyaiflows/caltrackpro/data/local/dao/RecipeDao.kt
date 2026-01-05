package com.easyaiflows.caltrackpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.easyaiflows.caltrackpro.data.local.entity.RecipeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {

    // Task 2.4: Basic CRUD operations

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity)

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("DELETE FROM recipes WHERE id = :id")
    suspend fun deleteById(id: String)

    // Task 2.5: Get all recipes for library screen
    @Query("SELECT * FROM recipes ORDER BY createdAt DESC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    // Task 2.6: Get recipe by ID for detail screen
    @Query("SELECT * FROM recipes WHERE id = :id")
    fun getRecipeById(id: String): Flow<RecipeEntity?>

    // Synchronous version for one-time reads
    @Query("SELECT * FROM recipes WHERE id = :id")
    suspend fun getRecipeByIdSync(id: String): RecipeEntity?

    // Task 2.7: Search recipes by name or description
    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchRecipes(query: String): Flow<List<RecipeEntity>>

    // Get recipes by category for filtering
    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY createdAt DESC")
    fun getRecipesByCategory(category: String): Flow<List<RecipeEntity>>

    // Get recipe count
    @Query("SELECT COUNT(*) FROM recipes")
    fun getRecipeCount(): Flow<Int>

    // Delete all recipes
    @Query("DELETE FROM recipes")
    suspend fun deleteAll()
}

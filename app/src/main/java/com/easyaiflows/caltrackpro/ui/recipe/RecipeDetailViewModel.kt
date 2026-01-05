package com.easyaiflows.caltrackpro.ui.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.data.repository.RecipeRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeDetailViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    private val foodEntryRepository: FoodEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState: StateFlow<RecipeDetailUiState> = _uiState.asStateFlow()

    init {
        val recipeId: String? = savedStateHandle["recipeId"]
        if (recipeId != null) {
            loadRecipe(recipeId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Recipe not found") }
        }
    }

    /**
     * Load recipe by ID.
     */
    private fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            recipeRepository.getRecipeById(recipeId).collect { recipe ->
                _uiState.update { state ->
                    state.copy(
                        recipe = recipe,
                        isLoading = false,
                        error = if (recipe == null) "Recipe not found" else null
                    )
                }
            }
        }
    }

    /**
     * Increment serving multiplier by 0.5 (max 5.0).
     */
    fun incrementServings() {
        _uiState.update { state ->
            val newMultiplier = (state.servingMultiplier + 0.5).coerceAtMost(5.0)
            state.copy(servingMultiplier = newMultiplier)
        }
    }

    /**
     * Decrement serving multiplier by 0.5 (min 0.5).
     */
    fun decrementServings() {
        _uiState.update { state ->
            val newMultiplier = (state.servingMultiplier - 0.5).coerceAtLeast(0.5)
            state.copy(servingMultiplier = newMultiplier)
        }
    }

    /**
     * Add recipe to food diary with scaled nutrition.
     */
    fun addToDiary(onSuccess: () -> Unit) {
        val recipe = _uiState.value.recipe ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToDiary = true) }

            try {
                val state = _uiState.value
                val mealType = getCurrentMealType()

                val foodEntry = FoodEntryEntity(
                    id = UUID.randomUUID().toString(),
                    name = recipe.name,
                    brand = null,
                    barcode = null,
                    calories = state.scaledCalories,
                    protein = state.scaledProtein,
                    carbs = state.scaledCarbs,
                    fat = state.scaledFat,
                    fiber = null,
                    sugar = null,
                    sodium = null,
                    servingSize = 1.0,
                    servingUnit = "serving",
                    quantity = state.servingMultiplier,
                    mealType = mealType.name,
                    timestamp = System.currentTimeMillis(),
                    imageData = recipe.imageData
                )

                foodEntryRepository.insert(foodEntry)

                _uiState.update { it.copy(isAddingToDiary = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isAddingToDiary = false) }
            }
        }
    }

    /**
     * Delete the recipe.
     */
    fun deleteRecipe(onSuccess: () -> Unit) {
        val recipe = _uiState.value.recipe ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            try {
                recipeRepository.deleteRecipe(recipe)
                _uiState.update { it.copy(isDeleting = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isDeleting = false) }
            }
        }
    }

    /**
     * Determine meal type based on current time.
     */
    private fun getCurrentMealType(): MealType {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        return when (hour) {
            in 5..10 -> MealType.BREAKFAST
            in 11..14 -> MealType.LUNCH
            in 15..17 -> MealType.SNACK
            in 18..21 -> MealType.DINNER
            else -> MealType.SNACK
        }
    }
}

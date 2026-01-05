package com.easyaiflows.caltrackpro.ui.recipe

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.RecipeRepository
import com.easyaiflows.caltrackpro.domain.model.Recipe
import com.easyaiflows.caltrackpro.domain.model.RecipeCategory
import com.easyaiflows.caltrackpro.domain.model.RecipeDifficulty
import com.easyaiflows.caltrackpro.domain.model.RecipeIngredient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class RecipeBuilderViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeBuilderUiState())
    val uiState: StateFlow<RecipeBuilderUiState> = _uiState.asStateFlow()

    // Track if recipe was loaded for edit mode
    private var existingRecipe: Recipe? = null

    init {
        // Check for recipeId in navigation arguments for edit mode
        val recipeId: String? = savedStateHandle["recipeId"]
        if (recipeId != null) {
            loadRecipe(recipeId)
        }
    }

    /**
     * Load an existing recipe for editing.
     */
    private fun loadRecipe(recipeId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val recipe = recipeRepository.getRecipeById(recipeId).firstOrNull()
            if (recipe != null) {
                existingRecipe = recipe
                _uiState.update { state ->
                    state.copy(
                        recipeId = recipe.id,
                        name = recipe.name,
                        description = recipe.description,
                        servings = recipe.servings,
                        cookingTimeMinutes = recipe.cookingTimeMinutes,
                        difficulty = recipe.difficulty,
                        category = recipe.category,
                        ingredients = recipe.ingredients,
                        instructions = recipe.instructions.ifEmpty { listOf("") },
                        imageData = recipe.imageData,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // Form field updates

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onServingsChange(servings: Int) {
        val validServings = servings.coerceIn(1, 100)
        _uiState.update { it.copy(servings = validServings) }
    }

    fun incrementServings() {
        _uiState.update { it.copy(servings = (it.servings + 1).coerceAtMost(100)) }
    }

    fun decrementServings() {
        _uiState.update { it.copy(servings = (it.servings - 1).coerceAtLeast(1)) }
    }

    fun onCookingTimeChange(minutes: Int) {
        val validTime = minutes.coerceIn(5, 180)
        _uiState.update { it.copy(cookingTimeMinutes = validTime) }
    }

    fun onDifficultyChange(difficulty: RecipeDifficulty) {
        _uiState.update { it.copy(difficulty = difficulty) }
    }

    fun onCategoryChange(category: RecipeCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onImageSelected(imageData: ByteArray?) {
        _uiState.update { it.copy(imageData = imageData) }
    }

    fun clearImage() {
        _uiState.update { it.copy(imageData = null) }
    }

    // Ingredient management

    fun showIngredientSearch() {
        _uiState.update { it.copy(showIngredientSearch = true) }
    }

    fun hideIngredientSearch() {
        _uiState.update { it.copy(showIngredientSearch = false) }
    }

    fun addIngredient(ingredient: RecipeIngredient) {
        _uiState.update { state ->
            state.copy(
                ingredients = state.ingredients + ingredient,
                ingredientsError = null,
                showIngredientSearch = false
            )
        }
    }

    fun removeIngredient(index: Int) {
        _uiState.update { state ->
            state.copy(
                ingredients = state.ingredients.toMutableList().apply {
                    if (index in indices) removeAt(index)
                }
            )
        }
    }

    fun updateIngredientQuantity(index: Int, quantity: Double) {
        _uiState.update { state ->
            val updatedIngredients = state.ingredients.toMutableList()
            if (index in updatedIngredients.indices) {
                val ingredient = updatedIngredients[index]
                updatedIngredients[index] = ingredient.copy(quantity = quantity)
            }
            state.copy(ingredients = updatedIngredients, editingIngredientIndex = null)
        }
    }

    fun startEditingIngredient(index: Int) {
        _uiState.update { it.copy(editingIngredientIndex = index) }
    }

    fun stopEditingIngredient() {
        _uiState.update { it.copy(editingIngredientIndex = null) }
    }

    // Instruction management

    fun addInstruction() {
        _uiState.update { state ->
            state.copy(instructions = state.instructions + "")
        }
    }

    fun removeInstruction(index: Int) {
        _uiState.update { state ->
            if (state.instructions.size > 1 && index in state.instructions.indices) {
                state.copy(
                    instructions = state.instructions.toMutableList().apply { removeAt(index) }
                )
            } else {
                state
            }
        }
    }

    fun updateInstruction(index: Int, text: String) {
        _uiState.update { state ->
            val updatedInstructions = state.instructions.toMutableList()
            if (index in updatedInstructions.indices) {
                updatedInstructions[index] = text
            }
            state.copy(instructions = updatedInstructions)
        }
    }

    // Validation and Save

    /**
     * Validate the form and return true if valid.
     */
    private fun validate(): Boolean {
        var isValid = true

        if (_uiState.value.name.isBlank()) {
            _uiState.update { it.copy(nameError = "Recipe name is required") }
            isValid = false
        }

        if (_uiState.value.ingredients.isEmpty()) {
            _uiState.update { it.copy(ingredientsError = "Add at least one ingredient") }
            isValid = false
        }

        return isValid
    }

    /**
     * Save the recipe and return true on success.
     */
    fun saveRecipe(onSuccess: () -> Unit) {
        if (!validate()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            try {
                val state = _uiState.value

                // Filter out empty instructions
                val cleanedInstructions = state.instructions.filter { it.isNotBlank() }

                val recipe = Recipe(
                    id = state.recipeId ?: UUID.randomUUID().toString(),
                    name = state.name.trim(),
                    description = state.description.trim(),
                    ingredients = state.ingredients,
                    instructions = cleanedInstructions,
                    servings = state.servings,
                    cookingTimeMinutes = state.cookingTimeMinutes,
                    difficulty = state.difficulty,
                    category = state.category,
                    imageData = state.imageData,
                    createdAt = existingRecipe?.createdAt ?: System.currentTimeMillis()
                )

                if (state.isEditMode) {
                    recipeRepository.updateRecipe(recipe)
                } else {
                    recipeRepository.insertRecipe(recipe)
                }

                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}

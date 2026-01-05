package com.easyaiflows.caltrackpro.ui.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.RecipeRepository
import com.easyaiflows.caltrackpro.domain.model.RecipeCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipeLibraryViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeLibraryUiState())
    val uiState: StateFlow<RecipeLibraryUiState> = _uiState.asStateFlow()

    init {
        loadRecipes()
    }

    /**
     * Load all recipes from the repository.
     */
    private fun loadRecipes() {
        viewModelScope.launch {
            recipeRepository.getAllRecipes().collect { recipes ->
                _uiState.update { state ->
                    state.copy(
                        recipes = recipes,
                        isLoading = false
                    )
                }
            }
        }
    }

    /**
     * Update the search query.
     */
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    /**
     * Clear the search query.
     */
    fun clearSearch() {
        _uiState.update { it.copy(searchQuery = "") }
    }

    /**
     * Set the category filter.
     */
    fun onCategorySelected(category: RecipeCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    /**
     * Set the sort order.
     */
    fun onSortOrderSelected(sortOrder: RecipeSortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
    }

    /**
     * Delete a recipe by ID.
     */
    fun deleteRecipe(recipeId: String) {
        viewModelScope.launch {
            recipeRepository.deleteRecipeById(recipeId)
        }
    }
}

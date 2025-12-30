package com.easyaiflows.caltrackpro.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.domain.model.ServingMeasure
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    private val foodSearchRepository: FoodSearchRepository,
    private val foodEntryRepository: FoodEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val foodId: String = savedStateHandle.get<String>("foodId") ?: ""
    private val mealTypeArg: String = savedStateHandle.get<String>("mealType") ?: MealType.SNACK.name
    private val dateArg: String = savedStateHandle.get<String>("date") ?: LocalDate.now().toString()

    private val _uiState = MutableStateFlow(
        FoodDetailUiState(
            selectedMealType = MealType.valueOf(mealTypeArg)
        )
    )
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    private val date: LocalDate = LocalDate.parse(dateArg)

    init {
        loadFood()
    }

    private fun loadFood() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = foodSearchRepository.getFoodById(foodId)

            result.fold(
                onSuccess = { food ->
                    if (food != null) {
                        val defaultMeasure = food.defaultMeasure ?: ServingMeasure.per100g()
                        _uiState.update {
                            it.copy(
                                food = food,
                                selectedMeasure = defaultMeasure,
                                isLoading = false
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Food not found"
                            )
                        }
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Failed to load food"
                        )
                    }
                }
            )
        }
    }

    /**
     * Update the selected serving measure
     */
    fun selectMeasure(measure: ServingMeasure) {
        _uiState.update { it.copy(selectedMeasure = measure) }
    }

    /**
     * Update the serving quantity
     */
    fun updateQuantity(quantity: Double) {
        if (quantity > 0) {
            _uiState.update { it.copy(quantity = quantity) }
        }
    }

    /**
     * Increment the quantity
     */
    fun incrementQuantity() {
        _uiState.update { it.copy(quantity = it.quantity + 0.5) }
    }

    /**
     * Decrement the quantity (minimum 0.5)
     */
    fun decrementQuantity() {
        _uiState.update {
            it.copy(quantity = maxOf(0.5, it.quantity - 0.5))
        }
    }

    /**
     * Update the selected meal type
     */
    fun selectMealType(mealType: MealType) {
        _uiState.update { it.copy(selectedMealType = mealType) }
    }

    /**
     * Add the food to the diary
     */
    fun addToDiary() {
        val state = _uiState.value
        val food = state.food ?: return
        val measure = state.selectedMeasure ?: return
        val nutrition = state.calculatedNutrition ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val timestamp = date.atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() + (12 * 60 * 60 * 1000) // Noon on selected date

                val entry = FoodEntryEntity(
                    id = UUID.randomUUID().toString(),
                    name = food.name,
                    brand = food.brand,
                    barcode = null,
                    calories = nutrition.calories,
                    protein = nutrition.protein,
                    carbs = nutrition.carbs,
                    fat = nutrition.fat,
                    fiber = nutrition.fiber,
                    sugar = nutrition.sugar,
                    sodium = nutrition.sodium,
                    servingSize = measure.weightGrams,
                    servingUnit = measure.label,
                    quantity = state.quantity,
                    mealType = state.selectedMealType.name,
                    timestamp = timestamp,
                    imageData = null
                )

                foodEntryRepository.insert(entry)

                _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = e.message ?: "Failed to save entry"
                    )
                }
            }
        }
    }

    /**
     * Clear the save success state
     */
    fun clearSaveSuccess() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
}

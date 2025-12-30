package com.easyaiflows.caltrackpro.ui.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.local.entity.FoodEntryEntity
import com.easyaiflows.caltrackpro.data.repository.FoodEntryRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * Available serving units
 */
enum class ServingUnit(val displayName: String, val abbreviation: String) {
    GRAMS("Grams", "g"),
    OUNCES("Ounces", "oz"),
    CUP("Cup", "cup"),
    PIECE("Piece", "pc"),
    MILLILITERS("Milliliters", "ml"),
    TABLESPOON("Tablespoon", "tbsp"),
    TEASPOON("Teaspoon", "tsp")
}

/**
 * UI state for the Manual Entry screen
 */
data class ManualEntryUiState(
    val isEditMode: Boolean = false,
    val entryId: String? = null,
    val name: String = "",
    val brand: String = "",
    val calories: String = "",
    val protein: String = "",
    val carbs: String = "",
    val fat: String = "",
    val fiber: String = "",
    val sugar: String = "",
    val sodium: String = "",
    val servingSize: String = "",
    val servingUnit: ServingUnit = ServingUnit.GRAMS,
    val quantity: String = "1",
    val mealType: MealType = MealType.SNACK,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = name.isNotBlank() &&
                calories.toDoubleOrNull() != null &&
                protein.toDoubleOrNull() != null &&
                carbs.toDoubleOrNull() != null &&
                fat.toDoubleOrNull() != null &&
                servingSize.toDoubleOrNull() != null &&
                servingSize.toDoubleOrNull()!! > 0 &&
                quantity.toDoubleOrNull() != null &&
                quantity.toDoubleOrNull()!! > 0
}

@HiltViewModel
class ManualEntryViewModel @Inject constructor(
    private val repository: FoodEntryRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManualEntryUiState())
    val uiState: StateFlow<ManualEntryUiState> = _uiState.asStateFlow()

    init {
        // Check if editing an existing entry
        val entryId: String? = savedStateHandle["entryId"]
        val mealTypeArg: String? = savedStateHandle["mealType"]

        if (entryId != null) {
            loadEntry(entryId)
        } else if (mealTypeArg != null) {
            _uiState.update { it.copy(mealType = MealType.valueOf(mealTypeArg)) }
        }
    }

    private fun loadEntry(entryId: String) {
        viewModelScope.launch {
            repository.getById(entryId)?.let { entity ->
                _uiState.update {
                    ManualEntryUiState(
                        isEditMode = true,
                        entryId = entity.id,
                        name = entity.name,
                        brand = entity.brand ?: "",
                        calories = entity.calories.formatForInput(),
                        protein = entity.protein.formatForInput(),
                        carbs = entity.carbs.formatForInput(),
                        fat = entity.fat.formatForInput(),
                        fiber = entity.fiber.formatForInput(),
                        sugar = entity.sugar.formatForInput(),
                        sodium = entity.sodium.formatForInput(),
                        servingSize = entity.servingSize.formatForInput(),
                        servingUnit = ServingUnit.entries.find { it.abbreviation == entity.servingUnit }
                            ?: ServingUnit.GRAMS,
                        quantity = entity.quantity.formatForInput(),
                        mealType = MealType.valueOf(entity.mealType)
                    )
                }
            }
        }
    }

    fun updateName(value: String) {
        _uiState.update { it.copy(name = value, error = null) }
    }

    fun updateBrand(value: String) {
        _uiState.update { it.copy(brand = value) }
    }

    fun updateCalories(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(calories = value, error = null) }
        }
    }

    fun updateProtein(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(protein = value, error = null) }
        }
    }

    fun updateCarbs(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(carbs = value, error = null) }
        }
    }

    fun updateFat(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(fat = value, error = null) }
        }
    }

    fun updateFiber(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(fiber = value) }
        }
    }

    fun updateSugar(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(sugar = value) }
        }
    }

    fun updateSodium(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(sodium = value) }
        }
    }

    fun updateServingSize(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(servingSize = value, error = null) }
        }
    }

    fun updateServingUnit(unit: ServingUnit) {
        _uiState.update { it.copy(servingUnit = unit) }
    }

    fun updateQuantity(value: String) {
        if (value.isEmpty() || value.isValidNumber()) {
            _uiState.update { it.copy(quantity = value, error = null) }
        }
    }

    fun updateMealType(mealType: MealType) {
        _uiState.update { it.copy(mealType = mealType) }
    }

    fun save() {
        val state = _uiState.value
        if (!state.isValid) {
            _uiState.update { it.copy(error = "Please fill in all required fields") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, error = null) }

            try {
                val entity = FoodEntryEntity(
                    id = state.entryId ?: UUID.randomUUID().toString(),
                    name = state.name.trim(),
                    brand = state.brand.trim().takeIf { it.isNotEmpty() },
                    barcode = null,
                    calories = state.calories.toDouble(),
                    protein = state.protein.toDouble(),
                    carbs = state.carbs.toDouble(),
                    fat = state.fat.toDouble(),
                    fiber = state.fiber.toDoubleOrNull() ?: 0.0,
                    sugar = state.sugar.toDoubleOrNull() ?: 0.0,
                    sodium = state.sodium.toDoubleOrNull() ?: 0.0,
                    servingSize = state.servingSize.toDouble(),
                    servingUnit = state.servingUnit.abbreviation,
                    quantity = state.quantity.toDouble(),
                    mealType = state.mealType.name,
                    timestamp = System.currentTimeMillis(),
                    imageData = null
                )

                if (state.isEditMode) {
                    repository.update(entity)
                } else {
                    repository.insert(entity)
                }

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

    private fun String.isValidNumber(): Boolean {
        return this.matches(Regex("^\\d*\\.?\\d*$"))
    }

    private fun Double.formatForInput(): String {
        return if (this == this.toLong().toDouble()) {
            this.toLong().toString()
        } else {
            this.toString()
        }
    }
}

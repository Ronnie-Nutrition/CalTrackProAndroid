package com.easyaiflows.caltrackpro.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.UserProfileRepository
import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.CustomMacros
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import com.easyaiflows.caltrackpro.util.NutritionCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val profile = userProfileRepository.userProfile.first()
                _uiState.update { ProfileUiState.fromUserProfile(profile) }
                recalculateNutrition()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, saveError = e.message) }
            }
        }
    }

    // Section expansion

    fun togglePersonalInfoExpanded() {
        _uiState.update { it.copy(personalInfoExpanded = !it.personalInfoExpanded) }
    }

    fun toggleBodyMetricsExpanded() {
        _uiState.update { it.copy(bodyMetricsExpanded = !it.bodyMetricsExpanded) }
    }

    fun toggleGoalsExpanded() {
        _uiState.update { it.copy(goalsExpanded = !it.goalsExpanded) }
    }

    fun togglePreferencesExpanded() {
        _uiState.update { it.copy(preferencesExpanded = !it.preferencesExpanded) }
    }

    // Personal Info

    fun updateAge(age: Int) {
        _uiState.update { state ->
            val error = when {
                age < UserProfile.MIN_AGE -> "Age must be at least ${UserProfile.MIN_AGE}"
                age > UserProfile.MAX_AGE -> "Age must be at most ${UserProfile.MAX_AGE}"
                else -> null
            }
            state.copy(age = age, ageError = error)
        }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateSex(sex: Sex) {
        _uiState.update { it.copy(sex = sex) }
        recalculateNutrition()
        saveIfValid()
    }

    // Body Metrics

    fun updateWeight(weightKg: Double) {
        _uiState.update { state ->
            val error = when {
                weightKg < UserProfile.MIN_WEIGHT_KG -> "Weight too low"
                weightKg > UserProfile.MAX_WEIGHT_KG -> "Weight too high"
                else -> null
            }
            state.copy(weightKg = weightKg, weightError = error)
        }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateHeight(heightCm: Double) {
        _uiState.update { state ->
            val error = when {
                heightCm < UserProfile.MIN_HEIGHT_CM -> "Height too low"
                heightCm > UserProfile.MAX_HEIGHT_CM -> "Height too high"
                else -> null
            }
            state.copy(heightCm = heightCm, heightError = error)
        }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateHeightFeetInches(feet: Int, inches: Int) {
        val heightCm = UnitSystem.feetInchesToCm(feet, inches)
        updateHeight(heightCm)
    }

    fun updateUnitSystem(unitSystem: UnitSystem) {
        _uiState.update { it.copy(unitSystem = unitSystem) }
        saveIfValid()
    }

    // Goals

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = activityLevel) }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateWeightGoal(weightGoal: WeightGoal) {
        _uiState.update { it.copy(weightGoal = weightGoal) }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateMacroPreset(macroPreset: MacroPreset) {
        _uiState.update { it.copy(macroPreset = macroPreset) }
        recalculateNutrition()
        saveIfValid()
    }

    fun updateCustomMacros(proteinPct: Int, carbsPct: Int, fatPct: Int) {
        if (proteinPct + carbsPct + fatPct == 100) {
            _uiState.update {
                it.copy(customMacros = CustomMacros(proteinPct, carbsPct, fatPct))
            }
            recalculateNutrition()
            saveIfValid()
        }
    }

    fun setCalorieOverride(calories: Int?) {
        _uiState.update { it.copy(calorieOverride = calories) }
        recalculateNutrition()
        saveIfValid()
    }

    fun resetToCalculated() {
        _uiState.update { it.copy(calorieOverride = null) }
        recalculateNutrition()
        saveIfValid()
    }

    // Calculations

    private fun recalculateNutrition() {
        _uiState.update { state ->
            val targets = NutritionCalculator.calculateAllTargets(state.toUserProfile())
            state.copy(
                calculatedCalories = targets.targetCalories,
                calculatedProtein = targets.proteinGrams,
                calculatedCarbs = targets.carbsGrams,
                calculatedFat = targets.fatGrams
            )
        }
    }

    // Save

    private fun saveIfValid() {
        val state = _uiState.value
        if (!state.hasErrors) {
            viewModelScope.launch {
                try {
                    _uiState.update { it.copy(isSaving = true, saveError = null) }
                    userProfileRepository.saveProfile(state.toUserProfile())
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                    // Reset success flag after a short delay
                    delay(2000)
                    _uiState.update { it.copy(saveSuccess = false) }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isSaving = false, saveError = e.message ?: "Failed to save")
                    }
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(saveError = null) }
    }
}

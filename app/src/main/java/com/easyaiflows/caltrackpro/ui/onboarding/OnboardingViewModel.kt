package com.easyaiflows.caltrackpro.ui.onboarding

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    init {
        // Calculate initial values
        recalculateNutrition()
    }

    // Navigation

    fun nextPage() {
        _uiState.update { state ->
            val currentIndex = state.currentPage.ordinal
            if (currentIndex < OnboardingPage.entries.size - 1) {
                state.copy(currentPage = OnboardingPage.entries[currentIndex + 1])
            } else {
                state
            }
        }
    }

    fun previousPage() {
        _uiState.update { state ->
            val currentIndex = state.currentPage.ordinal
            if (currentIndex > 0) {
                state.copy(currentPage = OnboardingPage.entries[currentIndex - 1])
            } else {
                state
            }
        }
    }

    fun goToPage(page: OnboardingPage) {
        _uiState.update { it.copy(currentPage = page) }
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
    }

    fun updateSex(sex: Sex) {
        _uiState.update { it.copy(sex = sex) }
        recalculateNutrition()
    }

    // Body Metrics

    fun updateWeight(weight: Double, inUserUnits: Boolean = true) {
        val weightKg = if (inUserUnits && _uiState.value.unitSystem == UnitSystem.IMPERIAL) {
            UnitSystem.lbsToKg(weight)
        } else {
            weight
        }

        _uiState.update { state ->
            val error = when {
                weightKg < UserProfile.MIN_WEIGHT_KG -> "Weight must be at least ${UserProfile.MIN_WEIGHT_KG.toInt()} kg"
                weightKg > UserProfile.MAX_WEIGHT_KG -> "Weight must be at most ${UserProfile.MAX_WEIGHT_KG.toInt()} kg"
                else -> null
            }
            state.copy(weightKg = weightKg, weightError = error)
        }
        recalculateNutrition()
    }

    fun updateHeight(height: Double, inUserUnits: Boolean = true) {
        val heightCm = if (inUserUnits && _uiState.value.unitSystem == UnitSystem.IMPERIAL) {
            height // Assuming total inches passed
        } else {
            height
        }

        _uiState.update { state ->
            val error = when {
                heightCm < UserProfile.MIN_HEIGHT_CM -> "Height must be at least ${UserProfile.MIN_HEIGHT_CM.toInt()} cm"
                heightCm > UserProfile.MAX_HEIGHT_CM -> "Height must be at most ${UserProfile.MAX_HEIGHT_CM.toInt()} cm"
                else -> null
            }
            state.copy(heightCm = heightCm, heightError = error)
        }
        recalculateNutrition()
    }

    fun updateHeightFeetInches(feet: Int, inches: Int) {
        val heightCm = UnitSystem.feetInchesToCm(feet, inches)
        _uiState.update { state ->
            val error = when {
                heightCm < UserProfile.MIN_HEIGHT_CM -> "Height must be at least ${UserProfile.MIN_HEIGHT_CM.toInt()} cm"
                heightCm > UserProfile.MAX_HEIGHT_CM -> "Height must be at most ${UserProfile.MAX_HEIGHT_CM.toInt()} cm"
                else -> null
            }
            state.copy(heightCm = heightCm, heightError = error)
        }
        recalculateNutrition()
    }

    fun updateUnitSystem(unitSystem: UnitSystem) {
        _uiState.update { it.copy(unitSystem = unitSystem) }
    }

    // Goals

    fun updateActivityLevel(activityLevel: ActivityLevel) {
        _uiState.update { it.copy(activityLevel = activityLevel) }
        recalculateNutrition()
    }

    fun updateWeightGoal(weightGoal: WeightGoal) {
        _uiState.update { it.copy(weightGoal = weightGoal) }
        recalculateNutrition()
    }

    fun updateMacroPreset(macroPreset: MacroPreset) {
        _uiState.update { it.copy(macroPreset = macroPreset) }
        recalculateNutrition()
    }

    fun updateCustomMacros(proteinPct: Int, carbsPct: Int, fatPct: Int) {
        if (proteinPct + carbsPct + fatPct == 100) {
            _uiState.update {
                it.copy(customMacros = CustomMacros(proteinPct, carbsPct, fatPct))
            }
            recalculateNutrition()
        }
    }

    // Override

    fun setCalorieOverride(calories: Int?) {
        _uiState.update { it.copy(calorieOverride = calories) }
        recalculateNutrition()
    }

    // Calculations

    private fun recalculateNutrition() {
        _uiState.update { state ->
            val bmr = NutritionCalculator.calculateBMR(
                state.sex,
                state.weightKg,
                state.heightCm,
                state.age
            )
            val tdee = NutritionCalculator.calculateTDEE(bmr, state.activityLevel)
            val targetCalories = NutritionCalculator.calculateTargetCalories(tdee, state.weightGoal)

            val effectiveCalories = state.calorieOverride ?: targetCalories
            val (proteinPct, carbsPct, fatPct) = if (state.macroPreset == MacroPreset.CUSTOM) {
                Triple(
                    state.customMacros.proteinPercent,
                    state.customMacros.carbsPercent,
                    state.customMacros.fatPercent
                )
            } else {
                Triple(
                    state.macroPreset.proteinPercent,
                    state.macroPreset.carbsPercent,
                    state.macroPreset.fatPercent
                )
            }

            val (protein, carbs, fat) = NutritionCalculator.calculateMacroGrams(
                effectiveCalories,
                proteinPct,
                carbsPct,
                fatPct
            )

            state.copy(
                calculatedBMR = bmr,
                calculatedTDEE = tdee,
                calculatedCalories = targetCalories,
                calculatedProtein = protein,
                calculatedCarbs = carbs,
                calculatedFat = fat
            )
        }
    }

    // Save

    fun saveProfile(onComplete: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, saveError = null) }
            try {
                val profile = _uiState.value.toUserProfile()
                userProfileRepository.saveProfile(profile)
                _uiState.update { it.copy(isSaving = false) }
                onComplete()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isSaving = false, saveError = e.message ?: "Failed to save profile")
                }
            }
        }
    }
}

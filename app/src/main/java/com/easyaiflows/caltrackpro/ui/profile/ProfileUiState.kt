package com.easyaiflows.caltrackpro.ui.profile

import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.CustomMacros
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal

/**
 * UI state for the Profile/Settings screen.
 */
data class ProfileUiState(
    // Profile data
    val age: Int = UserProfile.DEFAULT_AGE,
    val sex: Sex = Sex.Default,
    val weightKg: Double = UserProfile.DEFAULT_WEIGHT_KG,
    val heightCm: Double = UserProfile.DEFAULT_HEIGHT_CM,
    val activityLevel: ActivityLevel = ActivityLevel.Default,
    val weightGoal: WeightGoal = WeightGoal.Default,
    val macroPreset: MacroPreset = MacroPreset.Default,
    val customMacros: CustomMacros = CustomMacros.Default,
    val unitSystem: UnitSystem = UnitSystem.Default,
    val calorieOverride: Int? = null,

    // Calculated values
    val calculatedCalories: Int = 0,
    val calculatedProtein: Int = 0,
    val calculatedCarbs: Int = 0,
    val calculatedFat: Int = 0,

    // Validation errors
    val ageError: String? = null,
    val weightError: String? = null,
    val heightError: String? = null,

    // UI state
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val saveError: String? = null,

    // Expansion state for sections
    val personalInfoExpanded: Boolean = false,
    val bodyMetricsExpanded: Boolean = false,
    val goalsExpanded: Boolean = true,
    val preferencesExpanded: Boolean = false
) {
    /**
     * Get effective calorie target
     */
    val effectiveCalories: Int
        get() = calorieOverride ?: calculatedCalories

    /**
     * Check if there are unsaved changes
     */
    val hasErrors: Boolean
        get() = ageError != null || weightError != null || heightError != null

    /**
     * Convert to UserProfile
     */
    fun toUserProfile(onboardingCompleted: Boolean = true): UserProfile {
        return UserProfile(
            age = age,
            sex = sex,
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = activityLevel,
            weightGoal = weightGoal,
            macroPreset = macroPreset,
            customMacros = customMacros,
            unitSystem = unitSystem,
            calorieOverride = calorieOverride,
            onboardingCompleted = onboardingCompleted
        )
    }

    companion object {
        fun fromUserProfile(profile: UserProfile): ProfileUiState {
            return ProfileUiState(
                age = profile.age,
                sex = profile.sex,
                weightKg = profile.weightKg,
                heightCm = profile.heightCm,
                activityLevel = profile.activityLevel,
                weightGoal = profile.weightGoal,
                macroPreset = profile.macroPreset,
                customMacros = profile.customMacros,
                unitSystem = profile.unitSystem,
                calorieOverride = profile.calorieOverride,
                isLoading = false
            )
        }
    }
}

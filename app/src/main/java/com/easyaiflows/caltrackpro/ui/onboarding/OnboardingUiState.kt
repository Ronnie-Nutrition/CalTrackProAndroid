package com.easyaiflows.caltrackpro.ui.onboarding

import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.CustomMacros
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal

/**
 * Onboarding page indices
 */
enum class OnboardingPage {
    WELCOME,
    PERSONAL_INFO,
    BODY_METRICS,
    ACTIVITY_LEVEL,
    WEIGHT_GOAL,
    MACRO_PRESET,
    REVIEW,
    COMPLETION
}

/**
 * UI state for the onboarding flow.
 */
data class OnboardingUiState(
    // Current page
    val currentPage: OnboardingPage = OnboardingPage.WELCOME,

    // Personal info
    val age: Int = UserProfile.DEFAULT_AGE,
    val sex: Sex = Sex.Default,

    // Body metrics (stored in metric internally)
    val weightKg: Double = UserProfile.DEFAULT_WEIGHT_KG,
    val heightCm: Double = UserProfile.DEFAULT_HEIGHT_CM,
    val unitSystem: UnitSystem = UnitSystem.Default,

    // Goals
    val activityLevel: ActivityLevel = ActivityLevel.Default,
    val weightGoal: WeightGoal = WeightGoal.Default,
    val macroPreset: MacroPreset = MacroPreset.Default,
    val customMacros: CustomMacros = CustomMacros.Default,

    // Override values (null means use calculated)
    val calorieOverride: Int? = null,

    // Calculated values (updated when inputs change)
    val calculatedBMR: Double = 0.0,
    val calculatedTDEE: Double = 0.0,
    val calculatedCalories: Int = 0,
    val calculatedProtein: Int = 0,
    val calculatedCarbs: Int = 0,
    val calculatedFat: Int = 0,

    // Validation errors
    val ageError: String? = null,
    val weightError: String? = null,
    val heightError: String? = null,

    // UI state
    val isSaving: Boolean = false,
    val saveError: String? = null
) {
    /**
     * Get the effective calorie target (override or calculated)
     */
    val effectiveCalories: Int
        get() = calorieOverride ?: calculatedCalories

    /**
     * Check if current page has validation errors
     */
    val hasErrors: Boolean
        get() = ageError != null || weightError != null || heightError != null

    /**
     * Check if we can proceed from current page
     */
    fun canProceed(): Boolean {
        return when (currentPage) {
            OnboardingPage.WELCOME -> true
            OnboardingPage.PERSONAL_INFO -> ageError == null
            OnboardingPage.BODY_METRICS -> weightError == null && heightError == null
            OnboardingPage.ACTIVITY_LEVEL -> true
            OnboardingPage.WEIGHT_GOAL -> true
            OnboardingPage.MACRO_PRESET -> true
            OnboardingPage.REVIEW -> true
            OnboardingPage.COMPLETION -> true
        }
    }

    /**
     * Convert to UserProfile for saving
     */
    fun toUserProfile(): UserProfile {
        return UserProfile(
            age = age,
            sex = sex,
            weightKg = weightKg,
            heightCm = heightCm,
            activityLevel = activityLevel,
            weightGoal = weightGoal,
            macroPreset = macroPreset,
            unitSystem = unitSystem,
            customMacros = customMacros,
            calorieOverride = calorieOverride,
            onboardingCompleted = true
        )
    }

    companion object {
        val TOTAL_PAGES = OnboardingPage.entries.size
    }
}

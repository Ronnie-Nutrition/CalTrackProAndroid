package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.CustomMacros
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user profile operations.
 * Uses DataStore for persistence.
 */
interface UserProfileRepository {

    /**
     * Observe the current user profile.
     * Emits default profile if none exists.
     */
    val userProfile: Flow<UserProfile>

    /**
     * Check if onboarding has been completed.
     */
    val isOnboardingCompleted: Flow<Boolean>

    /**
     * Save the complete user profile.
     */
    suspend fun saveProfile(profile: UserProfile)

    /**
     * Update personal info (age, sex).
     */
    suspend fun updatePersonalInfo(age: Int, sex: Sex)

    /**
     * Update body metrics (weight, height) in metric units.
     */
    suspend fun updateBodyMetrics(weightKg: Double, heightCm: Double)

    /**
     * Update activity level.
     */
    suspend fun updateActivityLevel(activityLevel: ActivityLevel)

    /**
     * Update weight goal.
     */
    suspend fun updateWeightGoal(weightGoal: WeightGoal)

    /**
     * Update macro preset.
     */
    suspend fun updateMacroPreset(macroPreset: MacroPreset)

    /**
     * Update custom macros (when preset is CUSTOM).
     */
    suspend fun updateCustomMacros(customMacros: CustomMacros)

    /**
     * Update unit system preference.
     */
    suspend fun updateUnitSystem(unitSystem: UnitSystem)

    /**
     * Set or clear calorie override.
     * Pass null to use calculated value.
     */
    suspend fun setCalorieOverride(calories: Int?)

    /**
     * Mark onboarding as completed.
     */
    suspend fun completeOnboarding()

    /**
     * Reset profile to defaults (for testing or re-onboarding).
     */
    suspend fun resetProfile()
}

package com.easyaiflows.caltrackpro.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.CustomMacros
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserProfileRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserProfileRepository {

    companion object {
        // Preference keys
        private val KEY_AGE = intPreferencesKey("user_age")
        private val KEY_SEX = stringPreferencesKey("user_sex")
        private val KEY_WEIGHT_KG = doublePreferencesKey("user_weight_kg")
        private val KEY_HEIGHT_CM = doublePreferencesKey("user_height_cm")
        private val KEY_ACTIVITY_LEVEL = stringPreferencesKey("user_activity_level")
        private val KEY_WEIGHT_GOAL = stringPreferencesKey("user_weight_goal")
        private val KEY_MACRO_PRESET = stringPreferencesKey("user_macro_preset")
        private val KEY_UNIT_SYSTEM = stringPreferencesKey("user_unit_system")
        private val KEY_CUSTOM_PROTEIN_PCT = intPreferencesKey("user_custom_protein_pct")
        private val KEY_CUSTOM_CARBS_PCT = intPreferencesKey("user_custom_carbs_pct")
        private val KEY_CUSTOM_FAT_PCT = intPreferencesKey("user_custom_fat_pct")
        private val KEY_CALORIE_OVERRIDE = intPreferencesKey("user_calorie_override")
        private val KEY_HAS_CALORIE_OVERRIDE = booleanPreferencesKey("user_has_calorie_override")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("user_onboarding_completed")
    }

    override val userProfile: Flow<UserProfile> = dataStore.data.map { preferences ->
        val customMacros = CustomMacros(
            proteinPercent = preferences[KEY_CUSTOM_PROTEIN_PCT] ?: CustomMacros.Default.proteinPercent,
            carbsPercent = preferences[KEY_CUSTOM_CARBS_PCT] ?: CustomMacros.Default.carbsPercent,
            fatPercent = preferences[KEY_CUSTOM_FAT_PCT] ?: CustomMacros.Default.fatPercent
        )

        val calorieOverride = if (preferences[KEY_HAS_CALORIE_OVERRIDE] == true) {
            preferences[KEY_CALORIE_OVERRIDE]
        } else {
            null
        }

        UserProfile(
            age = preferences[KEY_AGE] ?: UserProfile.DEFAULT_AGE,
            sex = Sex.fromString(preferences[KEY_SEX]),
            weightKg = preferences[KEY_WEIGHT_KG] ?: UserProfile.DEFAULT_WEIGHT_KG,
            heightCm = preferences[KEY_HEIGHT_CM] ?: UserProfile.DEFAULT_HEIGHT_CM,
            activityLevel = ActivityLevel.fromString(preferences[KEY_ACTIVITY_LEVEL]),
            weightGoal = WeightGoal.fromString(preferences[KEY_WEIGHT_GOAL]),
            macroPreset = MacroPreset.fromString(preferences[KEY_MACRO_PRESET]),
            unitSystem = UnitSystem.fromString(preferences[KEY_UNIT_SYSTEM]),
            customMacros = customMacros,
            calorieOverride = calorieOverride,
            onboardingCompleted = preferences[KEY_ONBOARDING_COMPLETED] ?: false
        )
    }

    override val isOnboardingCompleted: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    override suspend fun saveProfile(profile: UserProfile) {
        dataStore.edit { preferences ->
            preferences[KEY_AGE] = profile.age
            preferences[KEY_SEX] = profile.sex.name
            preferences[KEY_WEIGHT_KG] = profile.weightKg
            preferences[KEY_HEIGHT_CM] = profile.heightCm
            preferences[KEY_ACTIVITY_LEVEL] = profile.activityLevel.name
            preferences[KEY_WEIGHT_GOAL] = profile.weightGoal.name
            preferences[KEY_MACRO_PRESET] = profile.macroPreset.name
            preferences[KEY_UNIT_SYSTEM] = profile.unitSystem.name
            preferences[KEY_CUSTOM_PROTEIN_PCT] = profile.customMacros.proteinPercent
            preferences[KEY_CUSTOM_CARBS_PCT] = profile.customMacros.carbsPercent
            preferences[KEY_CUSTOM_FAT_PCT] = profile.customMacros.fatPercent
            preferences[KEY_HAS_CALORIE_OVERRIDE] = profile.calorieOverride != null
            profile.calorieOverride?.let { preferences[KEY_CALORIE_OVERRIDE] = it }
            preferences[KEY_ONBOARDING_COMPLETED] = profile.onboardingCompleted
        }
    }

    override suspend fun updatePersonalInfo(age: Int, sex: Sex) {
        dataStore.edit { preferences ->
            preferences[KEY_AGE] = age
            preferences[KEY_SEX] = sex.name
        }
    }

    override suspend fun updateBodyMetrics(weightKg: Double, heightCm: Double) {
        dataStore.edit { preferences ->
            preferences[KEY_WEIGHT_KG] = weightKg
            preferences[KEY_HEIGHT_CM] = heightCm
        }
    }

    override suspend fun updateActivityLevel(activityLevel: ActivityLevel) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVITY_LEVEL] = activityLevel.name
        }
    }

    override suspend fun updateWeightGoal(weightGoal: WeightGoal) {
        dataStore.edit { preferences ->
            preferences[KEY_WEIGHT_GOAL] = weightGoal.name
        }
    }

    override suspend fun updateMacroPreset(macroPreset: MacroPreset) {
        dataStore.edit { preferences ->
            preferences[KEY_MACRO_PRESET] = macroPreset.name
        }
    }

    override suspend fun updateCustomMacros(customMacros: CustomMacros) {
        dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_PROTEIN_PCT] = customMacros.proteinPercent
            preferences[KEY_CUSTOM_CARBS_PCT] = customMacros.carbsPercent
            preferences[KEY_CUSTOM_FAT_PCT] = customMacros.fatPercent
        }
    }

    override suspend fun updateUnitSystem(unitSystem: UnitSystem) {
        dataStore.edit { preferences ->
            preferences[KEY_UNIT_SYSTEM] = unitSystem.name
        }
    }

    override suspend fun setCalorieOverride(calories: Int?) {
        dataStore.edit { preferences ->
            preferences[KEY_HAS_CALORIE_OVERRIDE] = calories != null
            if (calories != null) {
                preferences[KEY_CALORIE_OVERRIDE] = calories
            } else {
                preferences.remove(KEY_CALORIE_OVERRIDE)
            }
        }
    }

    override suspend fun completeOnboarding() {
        dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = true
        }
    }

    override suspend fun resetProfile() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

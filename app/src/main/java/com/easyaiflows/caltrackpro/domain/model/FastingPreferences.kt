package com.easyaiflows.caltrackpro.domain.model

/**
 * Data class representing user preferences for fasting.
 */
data class FastingPreferences(
    val selectedSchedule: FastingSchedule = FastingSchedule.default(),
    val customFastingHours: Int = 16,
    val waterGoalGlasses: Int = 8,
    val remindersEnabled: Boolean = true
)

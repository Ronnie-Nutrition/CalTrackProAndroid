package com.easyaiflows.caltrackpro.domain.model

/**
 * Activity level used to calculate TDEE (Total Daily Energy Expenditure).
 * Each level has a multiplier applied to BMR.
 */
enum class ActivityLevel(
    val multiplier: Double,
    val displayName: String,
    val description: String
) {
    SEDENTARY(
        multiplier = 1.2,
        displayName = "Sedentary",
        description = "Little or no exercise, desk job"
    ),
    ACTIVE(
        multiplier = 1.55,
        displayName = "Active",
        description = "Moderate exercise 3-5 days/week"
    ),
    VERY_ACTIVE(
        multiplier = 1.9,
        displayName = "Very Active",
        description = "Hard exercise 6-7 days/week"
    );

    companion object {
        /**
         * Default activity level for new profiles
         */
        val Default = SEDENTARY

        /**
         * Get ActivityLevel from string, with fallback to default
         */
        fun fromString(value: String?): ActivityLevel {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Default
        }
    }
}

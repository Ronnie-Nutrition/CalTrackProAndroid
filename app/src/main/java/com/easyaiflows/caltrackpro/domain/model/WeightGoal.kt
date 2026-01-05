package com.easyaiflows.caltrackpro.domain.model

/**
 * Weight goal used to adjust calorie target.
 * Each goal has a calorie adjustment applied to TDEE.
 */
enum class WeightGoal(
    val calorieAdjustment: Int,
    val displayName: String,
    val description: String
) {
    LOSE(
        calorieAdjustment = -500,
        displayName = "Lose Weight",
        description = "Lose ~1 lb per week"
    ),
    MAINTAIN(
        calorieAdjustment = 0,
        displayName = "Maintain",
        description = "Keep current weight"
    ),
    GAIN(
        calorieAdjustment = 300,
        displayName = "Gain Weight",
        description = "Lean muscle gain"
    );

    companion object {
        /**
         * Default weight goal for new profiles
         */
        val Default = MAINTAIN

        /**
         * Get WeightGoal from string, with fallback to default
         */
        fun fromString(value: String?): WeightGoal {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Default
        }
    }
}

package com.easyaiflows.caltrackpro.domain.model

/**
 * Macro distribution presets for protein, carbs, and fat percentages.
 * Percentages should sum to 100.
 */
enum class MacroPreset(
    val proteinPercent: Int,
    val carbsPercent: Int,
    val fatPercent: Int,
    val displayName: String,
    val description: String
) {
    BALANCED(
        proteinPercent = 30,
        carbsPercent = 40,
        fatPercent = 30,
        displayName = "Balanced",
        description = "General healthy eating"
    ),
    LOW_CARB(
        proteinPercent = 35,
        carbsPercent = 25,
        fatPercent = 40,
        displayName = "Low Carb",
        description = "Reduced carbohydrate intake"
    ),
    HIGH_PROTEIN(
        proteinPercent = 40,
        carbsPercent = 35,
        fatPercent = 25,
        displayName = "High Protein",
        description = "Muscle building focus"
    ),
    CUSTOM(
        proteinPercent = 0,
        carbsPercent = 0,
        fatPercent = 0,
        displayName = "Custom",
        description = "Set your own percentages"
    );

    companion object {
        /**
         * Default macro preset for new profiles
         */
        val Default = BALANCED

        /**
         * Get MacroPreset from string, with fallback to default
         */
        fun fromString(value: String?): MacroPreset {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Default
        }
    }
}

/**
 * Data class to hold custom macro percentages.
 * Used when MacroPreset.CUSTOM is selected.
 */
data class CustomMacros(
    val proteinPercent: Int = 30,
    val carbsPercent: Int = 40,
    val fatPercent: Int = 30
) {
    init {
        require(proteinPercent + carbsPercent + fatPercent == 100) {
            "Macro percentages must sum to 100"
        }
    }

    companion object {
        val Default = CustomMacros()
    }
}

package com.easyaiflows.caltrackpro.domain.model

/**
 * User profile containing personal information, body metrics, and nutrition goals.
 * All measurements are stored in metric units (kg, cm) internally.
 */
data class UserProfile(
    val age: Int = DEFAULT_AGE,
    val sex: Sex = Sex.Default,
    val weightKg: Double = DEFAULT_WEIGHT_KG,
    val heightCm: Double = DEFAULT_HEIGHT_CM,
    val activityLevel: ActivityLevel = ActivityLevel.Default,
    val weightGoal: WeightGoal = WeightGoal.Default,
    val macroPreset: MacroPreset = MacroPreset.Default,
    val unitSystem: UnitSystem = UnitSystem.Default,
    val customMacros: CustomMacros = CustomMacros.Default,
    val calorieOverride: Int? = null,
    val onboardingCompleted: Boolean = false
) {
    companion object {
        // Default values
        const val DEFAULT_AGE = 30
        const val DEFAULT_WEIGHT_KG = 70.0
        const val DEFAULT_HEIGHT_CM = 170.0

        // Validation constraints
        const val MIN_AGE = 13
        const val MAX_AGE = 120
        const val MIN_WEIGHT_KG = 20.0
        const val MAX_WEIGHT_KG = 500.0
        const val MIN_HEIGHT_CM = 100.0
        const val MAX_HEIGHT_CM = 250.0

        /**
         * Default profile for new users
         */
        val Default = UserProfile()
    }

    /**
     * Validation result for user profile fields
     */
    data class ValidationResult(
        val isValid: Boolean,
        val ageError: String? = null,
        val weightError: String? = null,
        val heightError: String? = null
    )

    /**
     * Validate all profile fields
     */
    fun validate(): ValidationResult {
        val ageError = validateAge()
        val weightError = validateWeight()
        val heightError = validateHeight()

        return ValidationResult(
            isValid = ageError == null && weightError == null && heightError == null,
            ageError = ageError,
            weightError = weightError,
            heightError = heightError
        )
    }

    /**
     * Validate age field
     * @return Error message or null if valid
     */
    fun validateAge(): String? {
        return when {
            age < MIN_AGE -> "Age must be at least $MIN_AGE"
            age > MAX_AGE -> "Age must be at most $MAX_AGE"
            else -> null
        }
    }

    /**
     * Validate weight field
     * @return Error message or null if valid
     */
    fun validateWeight(): String? {
        return when {
            weightKg < MIN_WEIGHT_KG -> "Weight must be at least ${MIN_WEIGHT_KG.toInt()} kg"
            weightKg > MAX_WEIGHT_KG -> "Weight must be at most ${MAX_WEIGHT_KG.toInt()} kg"
            else -> null
        }
    }

    /**
     * Validate height field
     * @return Error message or null if valid
     */
    fun validateHeight(): String? {
        return when {
            heightCm < MIN_HEIGHT_CM -> "Height must be at least ${MIN_HEIGHT_CM.toInt()} cm"
            heightCm > MAX_HEIGHT_CM -> "Height must be at most ${MAX_HEIGHT_CM.toInt()} cm"
            else -> null
        }
    }

    /**
     * Check if all required fields are filled for onboarding completion
     */
    fun isComplete(): Boolean {
        return validate().isValid
    }

    /**
     * Get the effective macro percentages (from preset or custom)
     */
    fun getEffectiveMacros(): Triple<Int, Int, Int> {
        return if (macroPreset == MacroPreset.CUSTOM) {
            Triple(
                customMacros.proteinPercent,
                customMacros.carbsPercent,
                customMacros.fatPercent
            )
        } else {
            Triple(
                macroPreset.proteinPercent,
                macroPreset.carbsPercent,
                macroPreset.fatPercent
            )
        }
    }

    /**
     * Get weight in the user's preferred unit system
     */
    fun getDisplayWeight(): Double {
        return when (unitSystem) {
            UnitSystem.METRIC -> weightKg
            UnitSystem.IMPERIAL -> UnitSystem.kgToLbs(weightKg)
        }
    }

    /**
     * Get height in the user's preferred unit system
     * For imperial, returns total inches
     */
    fun getDisplayHeight(): Double {
        return when (unitSystem) {
            UnitSystem.METRIC -> heightCm
            UnitSystem.IMPERIAL -> UnitSystem.cmToInches(heightCm)
        }
    }

    /**
     * Get formatted weight string
     */
    fun getFormattedWeight(): String {
        return UnitSystem.formatWeight(weightKg, unitSystem)
    }

    /**
     * Get formatted height string
     */
    fun getFormattedHeight(): String {
        return UnitSystem.formatHeight(heightCm, unitSystem)
    }
}

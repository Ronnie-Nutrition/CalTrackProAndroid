package com.easyaiflows.caltrackpro.domain.model

/**
 * Represents daily nutrition goals for the user.
 * Default values are based on general dietary recommendations.
 */
data class NutritionGoals(
    val calories: Int = DEFAULT_CALORIES,
    val protein: Int = DEFAULT_PROTEIN,
    val carbs: Int = DEFAULT_CARBS,
    val fat: Int = DEFAULT_FAT,
    val fiber: Int = DEFAULT_FIBER,
    val sugar: Int = DEFAULT_SUGAR,
    val sodium: Int = DEFAULT_SODIUM
) {
    companion object {
        const val DEFAULT_CALORIES = 2000
        const val DEFAULT_PROTEIN = 150  // grams
        const val DEFAULT_CARBS = 250    // grams
        const val DEFAULT_FAT = 65       // grams
        const val DEFAULT_FIBER = 25     // grams
        const val DEFAULT_SUGAR = 50     // grams
        const val DEFAULT_SODIUM = 2300  // milligrams

        /**
         * Default nutrition goals based on general dietary recommendations
         */
        val Default = NutritionGoals()
    }

    /**
     * Calculate the percentage of goal reached for calories
     */
    fun caloriesProgress(consumed: Double): Float {
        return if (calories > 0) (consumed / calories).toFloat().coerceIn(0f, 1f) else 0f
    }

    /**
     * Calculate the percentage of goal reached for protein
     */
    fun proteinProgress(consumed: Double): Float {
        return if (protein > 0) (consumed / protein).toFloat().coerceIn(0f, 1f) else 0f
    }

    /**
     * Calculate the percentage of goal reached for carbs
     */
    fun carbsProgress(consumed: Double): Float {
        return if (carbs > 0) (consumed / carbs).toFloat().coerceIn(0f, 1f) else 0f
    }

    /**
     * Calculate the percentage of goal reached for fat
     */
    fun fatProgress(consumed: Double): Float {
        return if (fat > 0) (consumed / fat).toFloat().coerceIn(0f, 1f) else 0f
    }

    /**
     * Calculate remaining calories for the day
     */
    fun remainingCalories(consumed: Double): Int {
        return (calories - consumed).toInt().coerceAtLeast(0)
    }

    /**
     * Calculate remaining protein for the day
     */
    fun remainingProtein(consumed: Double): Int {
        return (protein - consumed).toInt().coerceAtLeast(0)
    }

    /**
     * Calculate remaining carbs for the day
     */
    fun remainingCarbs(consumed: Double): Int {
        return (carbs - consumed).toInt().coerceAtLeast(0)
    }

    /**
     * Calculate remaining fat for the day
     */
    fun remainingFat(consumed: Double): Int {
        return (fat - consumed).toInt().coerceAtLeast(0)
    }
}

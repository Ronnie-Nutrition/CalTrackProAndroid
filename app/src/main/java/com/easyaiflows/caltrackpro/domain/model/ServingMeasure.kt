package com.easyaiflows.caltrackpro.domain.model

/**
 * Domain model representing a serving measure option.
 * Used to calculate nutrition for different serving sizes.
 */
data class ServingMeasure(
    val uri: String,
    val label: String,
    val weightGrams: Double
) {
    /**
     * Display text for the measure (e.g., "1 Cup (240g)").
     */
    val displayText: String
        get() = "$label (${weightGrams.toInt()}g)"

    companion object {
        /**
         * Create a gram-based measure for manual entry.
         */
        fun grams(): ServingMeasure = ServingMeasure(
            uri = "gram",
            label = "Gram",
            weightGrams = 1.0
        )

        /**
         * Create a 100g measure for reference.
         */
        fun per100g(): ServingMeasure = ServingMeasure(
            uri = "100g",
            label = "100g",
            weightGrams = 100.0
        )
    }
}

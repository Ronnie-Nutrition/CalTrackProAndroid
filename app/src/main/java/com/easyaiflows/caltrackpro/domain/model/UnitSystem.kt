package com.easyaiflows.caltrackpro.domain.model

import kotlin.math.roundToInt

/**
 * Unit system for weight and height measurements.
 */
enum class UnitSystem(
    val displayName: String,
    val weightUnit: String,
    val heightUnit: String
) {
    METRIC(
        displayName = "Metric",
        weightUnit = "kg",
        heightUnit = "cm"
    ),
    IMPERIAL(
        displayName = "Imperial",
        weightUnit = "lbs",
        heightUnit = "ft/in"
    );

    companion object {
        /**
         * Default unit system for new profiles
         */
        val Default = METRIC

        /**
         * Get UnitSystem from string, with fallback to default
         */
        fun fromString(value: String?): UnitSystem {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Default
        }

        // Weight conversions
        private const val LBS_PER_KG = 2.20462

        /**
         * Convert kilograms to pounds
         */
        fun kgToLbs(kg: Double): Double = kg * LBS_PER_KG

        /**
         * Convert pounds to kilograms
         */
        fun lbsToKg(lbs: Double): Double = lbs / LBS_PER_KG

        // Height conversions
        private const val INCHES_PER_CM = 0.393701
        private const val CM_PER_INCH = 2.54
        private const val INCHES_PER_FOOT = 12

        /**
         * Convert centimeters to inches
         */
        fun cmToInches(cm: Double): Double = cm * INCHES_PER_CM

        /**
         * Convert inches to centimeters
         */
        fun inchesToCm(inches: Double): Double = inches * CM_PER_INCH

        /**
         * Convert centimeters to feet and inches
         * @return Pair of (feet, inches)
         */
        fun cmToFeetInches(cm: Double): Pair<Int, Int> {
            val totalInches = cmToInches(cm)
            val feet = (totalInches / INCHES_PER_FOOT).toInt()
            val inches = (totalInches % INCHES_PER_FOOT).roundToInt()
            return feet to inches
        }

        /**
         * Convert feet and inches to centimeters
         */
        fun feetInchesToCm(feet: Int, inches: Int): Double {
            val totalInches = (feet * INCHES_PER_FOOT) + inches
            return inchesToCm(totalInches.toDouble())
        }

        /**
         * Format height for display based on unit system
         */
        fun formatHeight(heightCm: Double, unitSystem: UnitSystem): String {
            return when (unitSystem) {
                METRIC -> "${heightCm.roundToInt()} cm"
                IMPERIAL -> {
                    val (feet, inches) = cmToFeetInches(heightCm)
                    "$feet' $inches\""
                }
            }
        }

        /**
         * Format weight for display based on unit system
         */
        fun formatWeight(weightKg: Double, unitSystem: UnitSystem): String {
            return when (unitSystem) {
                METRIC -> "${String.format("%.1f", weightKg)} kg"
                IMPERIAL -> "${String.format("%.1f", kgToLbs(weightKg))} lbs"
            }
        }
    }
}

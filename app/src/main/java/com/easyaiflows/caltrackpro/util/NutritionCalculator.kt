package com.easyaiflows.caltrackpro.util

import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UserProfile
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import kotlin.math.roundToInt

/**
 * Nutrition calculation utilities using the Mifflin-St Jeor formula.
 * All weight inputs should be in kg, height in cm.
 */
object NutritionCalculator {

    // Constants for macro calculations
    private const val CALORIES_PER_GRAM_PROTEIN = 4.0
    private const val CALORIES_PER_GRAM_CARBS = 4.0
    private const val CALORIES_PER_GRAM_FAT = 9.0

    /**
     * Calculate Basal Metabolic Rate (BMR) using Mifflin-St Jeor formula.
     *
     * Male: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age) + 5
     * Female: BMR = (10 × weight in kg) + (6.25 × height in cm) - (5 × age) - 161
     *
     * @param sex The user's biological sex
     * @param weightKg Weight in kilograms
     * @param heightCm Height in centimeters
     * @param age Age in years
     * @return BMR in calories per day
     */
    fun calculateBMR(sex: Sex, weightKg: Double, heightCm: Double, age: Int): Double {
        val baseBMR = (10.0 * weightKg) + (6.25 * heightCm) - (5.0 * age)
        return when (sex) {
            Sex.MALE -> baseBMR + 5
            Sex.FEMALE -> baseBMR - 161
        }
    }

    /**
     * Calculate Total Daily Energy Expenditure (TDEE).
     * TDEE = BMR × activity multiplier
     *
     * @param bmr Basal Metabolic Rate
     * @param activityLevel Activity level with multiplier
     * @return TDEE in calories per day
     */
    fun calculateTDEE(bmr: Double, activityLevel: ActivityLevel): Double {
        return bmr * activityLevel.multiplier
    }

    /**
     * Calculate target calories based on TDEE and weight goal.
     *
     * @param tdee Total Daily Energy Expenditure
     * @param weightGoal Weight goal with calorie adjustment
     * @return Target calories per day
     */
    fun calculateTargetCalories(tdee: Double, weightGoal: WeightGoal): Int {
        return (tdee + weightGoal.calorieAdjustment).roundToInt()
    }

    /**
     * Calculate all values from a user profile.
     * Returns (BMR, TDEE, targetCalories) triple.
     *
     * @param profile User profile with all metrics
     * @return Triple of (BMR, TDEE, targetCalories)
     */
    fun calculateFromProfile(profile: UserProfile): Triple<Double, Double, Int> {
        val bmr = calculateBMR(profile.sex, profile.weightKg, profile.heightCm, profile.age)
        val tdee = calculateTDEE(bmr, profile.activityLevel)
        val targetCalories = profile.calorieOverride ?: calculateTargetCalories(tdee, profile.weightGoal)
        return Triple(bmr, tdee, targetCalories)
    }

    /**
     * Calculate macro grams from calorie percentages.
     *
     * @param calories Total daily calories
     * @param proteinPercent Percentage of calories from protein (0-100)
     * @param carbsPercent Percentage of calories from carbs (0-100)
     * @param fatPercent Percentage of calories from fat (0-100)
     * @return Triple of (proteinGrams, carbsGrams, fatGrams)
     */
    fun calculateMacroGrams(
        calories: Int,
        proteinPercent: Int,
        carbsPercent: Int,
        fatPercent: Int
    ): Triple<Int, Int, Int> {
        val proteinGrams = ((calories * proteinPercent / 100.0) / CALORIES_PER_GRAM_PROTEIN).roundToInt()
        val carbsGrams = ((calories * carbsPercent / 100.0) / CALORIES_PER_GRAM_CARBS).roundToInt()
        val fatGrams = ((calories * fatPercent / 100.0) / CALORIES_PER_GRAM_FAT).roundToInt()
        return Triple(proteinGrams, carbsGrams, fatGrams)
    }

    /**
     * Get macro grams for a specific preset.
     *
     * @param preset Macro preset to use
     * @param calories Total daily calories
     * @param customProtein Custom protein percentage (used when preset is CUSTOM)
     * @param customCarbs Custom carbs percentage (used when preset is CUSTOM)
     * @param customFat Custom fat percentage (used when preset is CUSTOM)
     * @return Triple of (proteinGrams, carbsGrams, fatGrams)
     */
    fun getMacrosForPreset(
        preset: MacroPreset,
        calories: Int,
        customProtein: Int = 30,
        customCarbs: Int = 40,
        customFat: Int = 30
    ): Triple<Int, Int, Int> {
        return when (preset) {
            MacroPreset.CUSTOM -> calculateMacroGrams(calories, customProtein, customCarbs, customFat)
            else -> calculateMacroGrams(
                calories,
                preset.proteinPercent,
                preset.carbsPercent,
                preset.fatPercent
            )
        }
    }

    /**
     * Calculate complete macro targets from user profile.
     *
     * @param profile User profile
     * @return Triple of (proteinGrams, carbsGrams, fatGrams)
     */
    fun calculateMacrosFromProfile(profile: UserProfile): Triple<Int, Int, Int> {
        val (_, _, targetCalories) = calculateFromProfile(profile)
        val (proteinPct, carbsPct, fatPct) = profile.getEffectiveMacros()
        return calculateMacroGrams(targetCalories, proteinPct, carbsPct, fatPct)
    }

    /**
     * Data class to hold all calculated nutrition values.
     */
    data class NutritionTargets(
        val bmr: Double,
        val tdee: Double,
        val targetCalories: Int,
        val proteinGrams: Int,
        val carbsGrams: Int,
        val fatGrams: Int
    )

    /**
     * Calculate all nutrition targets from a user profile.
     *
     * @param profile User profile
     * @return NutritionTargets with all calculated values
     */
    fun calculateAllTargets(profile: UserProfile): NutritionTargets {
        val (bmr, tdee, targetCalories) = calculateFromProfile(profile)
        val (proteinGrams, carbsGrams, fatGrams) = calculateMacrosFromProfile(profile)

        return NutritionTargets(
            bmr = bmr,
            tdee = tdee,
            targetCalories = targetCalories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams
        )
    }
}

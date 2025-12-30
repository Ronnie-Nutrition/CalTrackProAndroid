package com.easyaiflows.caltrackpro.ui.diary

import com.easyaiflows.caltrackpro.domain.model.FoodEntry
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.domain.model.NutritionGoals
import java.time.LocalDate

/**
 * UI state for the Diary screen
 */
data class DiaryUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val entries: List<FoodEntry> = emptyList(),
    val entriesByMeal: Map<MealType, List<FoodEntry>> = emptyMap(),
    val goals: NutritionGoals = NutritionGoals.Default,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    /**
     * Total calories consumed for the day
     */
    val totalCalories: Double
        get() = entries.sumOf { it.totalCalories }

    /**
     * Total protein consumed for the day
     */
    val totalProtein: Double
        get() = entries.sumOf { it.totalProtein }

    /**
     * Total carbs consumed for the day
     */
    val totalCarbs: Double
        get() = entries.sumOf { it.totalCarbs }

    /**
     * Total fat consumed for the day
     */
    val totalFat: Double
        get() = entries.sumOf { it.totalFat }

    /**
     * Remaining calories for the day
     */
    val remainingCalories: Int
        get() = goals.remainingCalories(totalCalories)

    /**
     * Progress towards calorie goal (0.0 to 1.0)
     */
    val caloriesProgress: Float
        get() = goals.caloriesProgress(totalCalories)

    /**
     * Progress towards protein goal (0.0 to 1.0)
     */
    val proteinProgress: Float
        get() = goals.proteinProgress(totalProtein)

    /**
     * Progress towards carbs goal (0.0 to 1.0)
     */
    val carbsProgress: Float
        get() = goals.carbsProgress(totalCarbs)

    /**
     * Progress towards fat goal (0.0 to 1.0)
     */
    val fatProgress: Float
        get() = goals.fatProgress(totalFat)

    /**
     * Check if the selected date is today
     */
    val isToday: Boolean
        get() = selectedDate == LocalDate.now()

    /**
     * Get calories for a specific meal type
     */
    fun caloriesForMeal(mealType: MealType): Double {
        return entriesByMeal[mealType]?.sumOf { it.totalCalories } ?: 0.0
    }
}

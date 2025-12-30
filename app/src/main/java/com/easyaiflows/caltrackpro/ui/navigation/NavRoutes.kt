package com.easyaiflows.caltrackpro.ui.navigation

import com.easyaiflows.caltrackpro.domain.model.MealType

/**
 * Navigation routes for the app
 */
sealed class NavRoutes(val route: String) {

    /**
     * Food Diary screen - main screen showing daily entries
     */
    data object Diary : NavRoutes("diary")

    /**
     * Manual Entry screen for adding new food entries
     * @param mealType The meal type to pre-select (BREAKFAST, LUNCH, DINNER, SNACK)
     */
    data object ManualEntry : NavRoutes("manual_entry/{mealType}") {
        const val ARG_MEAL_TYPE = "mealType"

        fun createRoute(mealType: MealType): String {
            return "manual_entry/${mealType.name}"
        }
    }

    /**
     * Edit Entry screen for modifying existing food entries
     * @param entryId The ID of the entry to edit
     */
    data object EditEntry : NavRoutes("edit_entry/{entryId}") {
        const val ARG_ENTRY_ID = "entryId"

        fun createRoute(entryId: String): String {
            return "edit_entry/$entryId"
        }
    }
}

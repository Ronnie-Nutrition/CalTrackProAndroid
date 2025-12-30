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

    /**
     * Food Search screen for searching and adding foods from Edamam API
     * @param mealType The meal type to add the food to
     * @param date The date to add the food entry for (ISO format)
     */
    data object FoodSearch : NavRoutes("food_search/{mealType}/{date}") {
        const val ARG_MEAL_TYPE = "mealType"
        const val ARG_DATE = "date"

        fun createRoute(mealType: MealType, date: String): String {
            return "food_search/${mealType.name}/$date"
        }
    }

    /**
     * Food Detail screen for viewing and adding a specific food
     * @param foodId The Edamam food ID
     * @param mealType The meal type to add the food to
     * @param date The date to add the food entry for (ISO format)
     */
    data object FoodDetail : NavRoutes("food_detail/{foodId}/{mealType}/{date}") {
        const val ARG_FOOD_ID = "foodId"
        const val ARG_MEAL_TYPE = "mealType"
        const val ARG_DATE = "date"

        fun createRoute(foodId: String, mealType: MealType, date: String): String {
            return "food_detail/$foodId/${mealType.name}/$date"
        }
    }
}

package com.easyaiflows.caltrackpro.ui.navigation

import com.easyaiflows.caltrackpro.domain.model.MealType

/**
 * Navigation routes for the app
 */
sealed class NavRoutes(val route: String) {

    /**
     * Onboarding screen - multi-page wizard for new users
     */
    data object Onboarding : NavRoutes("onboarding")

    /**
     * Profile/Settings screen - edit user profile and goals
     */
    data object Profile : NavRoutes("profile")

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

    /**
     * Barcode Scanner screen for scanning product barcodes
     * @param mealType The meal type to add the scanned food to
     * @param date The date to add the food entry for (ISO format)
     */
    data object BarcodeScanner : NavRoutes("barcode_scanner/{mealType}/{date}") {
        const val ARG_MEAL_TYPE = "mealType"
        const val ARG_DATE = "date"

        fun createRoute(mealType: MealType, date: String): String {
            return "barcode_scanner/${mealType.name}/$date"
        }
    }

    /**
     * Recipe Library screen - main screen showing all saved recipes
     */
    data object RecipeLibrary : NavRoutes("recipe_library")

    /**
     * Recipe Detail screen for viewing a specific recipe
     * @param recipeId The ID of the recipe to view
     */
    data object RecipeDetail : NavRoutes("recipe_detail/{recipeId}") {
        const val ARG_RECIPE_ID = "recipeId"

        fun createRoute(recipeId: String): String {
            return "recipe_detail/$recipeId"
        }
    }

    /**
     * Recipe Builder screen for creating or editing recipes
     * @param recipeId Optional ID for editing an existing recipe (null for create)
     */
    data object RecipeBuilder : NavRoutes("recipe_builder?recipeId={recipeId}") {
        const val ARG_RECIPE_ID = "recipeId"

        fun createRoute(recipeId: String? = null): String {
            return if (recipeId != null) {
                "recipe_builder?recipeId=$recipeId"
            } else {
                "recipe_builder"
            }
        }
    }
}

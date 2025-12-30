package com.easyaiflows.caltrackpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.ui.diary.DiaryScreen
import com.easyaiflows.caltrackpro.ui.entry.ManualEntryScreen
import com.easyaiflows.caltrackpro.ui.search.FoodDetailScreen
import com.easyaiflows.caltrackpro.ui.search.FoodSearchScreen
import java.time.LocalDate

@Composable
fun CalTrackNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = NavRoutes.Diary.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Diary Screen
        composable(route = NavRoutes.Diary.route) {
            DiaryScreen(
                onNavigateToManualEntry = { mealType ->
                    navController.navigate(NavRoutes.ManualEntry.createRoute(mealType))
                },
                onNavigateToEditEntry = { entryId ->
                    navController.navigate(NavRoutes.EditEntry.createRoute(entryId))
                },
                onNavigateToSearch = { mealType, date ->
                    navController.navigate(NavRoutes.FoodSearch.createRoute(mealType, date.toString()))
                }
            )
        }

        // Manual Entry Screen (new entry)
        composable(
            route = NavRoutes.ManualEntry.route,
            arguments = listOf(
                navArgument(NavRoutes.ManualEntry.ARG_MEAL_TYPE) {
                    type = NavType.StringType
                    defaultValue = MealType.SNACK.name
                }
            )
        ) {
            ManualEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Edit Entry Screen (existing entry)
        composable(
            route = NavRoutes.EditEntry.route,
            arguments = listOf(
                navArgument(NavRoutes.EditEntry.ARG_ENTRY_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            ManualEntryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Food Search Screen
        composable(
            route = NavRoutes.FoodSearch.route,
            arguments = listOf(
                navArgument(NavRoutes.FoodSearch.ARG_MEAL_TYPE) {
                    type = NavType.StringType
                    defaultValue = MealType.SNACK.name
                },
                navArgument(NavRoutes.FoodSearch.ARG_DATE) {
                    type = NavType.StringType
                    defaultValue = LocalDate.now().toString()
                }
            )
        ) { backStackEntry ->
            val mealType = backStackEntry.arguments?.getString(NavRoutes.FoodSearch.ARG_MEAL_TYPE)
                ?.let { MealType.valueOf(it) } ?: MealType.SNACK
            val date = backStackEntry.arguments?.getString(NavRoutes.FoodSearch.ARG_DATE)
                ?.let { LocalDate.parse(it) } ?: LocalDate.now()

            FoodSearchScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onFoodSelected = { food ->
                    navController.navigate(
                        NavRoutes.FoodDetail.createRoute(food.foodId, mealType, date.toString())
                    )
                }
            )
        }

        // Food Detail Screen
        composable(
            route = NavRoutes.FoodDetail.route,
            arguments = listOf(
                navArgument(NavRoutes.FoodDetail.ARG_FOOD_ID) {
                    type = NavType.StringType
                },
                navArgument(NavRoutes.FoodDetail.ARG_MEAL_TYPE) {
                    type = NavType.StringType
                    defaultValue = MealType.SNACK.name
                },
                navArgument(NavRoutes.FoodDetail.ARG_DATE) {
                    type = NavType.StringType
                    defaultValue = LocalDate.now().toString()
                }
            )
        ) {
            FoodDetailScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onAddSuccess = {
                    // Pop back to diary after adding food
                    navController.popBackStack(NavRoutes.Diary.route, inclusive = false)
                }
            )
        }
    }
}

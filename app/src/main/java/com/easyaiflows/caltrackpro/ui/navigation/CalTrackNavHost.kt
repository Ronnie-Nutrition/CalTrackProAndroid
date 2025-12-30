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
    }
}

package com.easyaiflows.caltrackpro.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BakeryDining
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Dining
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LunchDining
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Represents the category of a recipe for organization and filtering.
 */
enum class RecipeCategory(
    val displayName: String,
    val icon: ImageVector
) {
    BREAKFAST("Breakfast", Icons.Default.WbSunny),
    LUNCH("Lunch", Icons.Default.LunchDining),
    DINNER("Dinner", Icons.Default.DarkMode),
    SNACK("Snack", Icons.Default.BakeryDining),
    DESSERT("Dessert", Icons.Default.Cake),
    MAIN("Main", Icons.Default.Restaurant),
    SIDE("Side", Icons.Default.Dining),
    DRINK("Drink", Icons.Default.LocalCafe);

    companion object {
        fun fromString(value: String): RecipeCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: MAIN
        }
    }
}

package com.easyaiflows.caltrackpro.ui.recipe.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.domain.model.RecipeIngredient
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import com.easyaiflows.caltrackpro.domain.model.SimpleFoodItem
import kotlinx.coroutines.launch

/**
 * Bottom sheet for searching and adding ingredients to a recipe.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IngredientSearchSheet(
    foodSearchRepository: FoodSearchRepository,
    onIngredientSelected: (RecipeIngredient) -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchedFood>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    var selectedFood by remember { mutableStateOf<SearchedFood?>(null) }
    var quantity by remember { mutableStateOf("1") }

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Auto-focus search field
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Add Ingredient",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = { Text("Search for ingredients...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            searchResults = emptyList()
                            hasSearched = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear"
                            )
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchQuery.length >= 2) {
                            focusManager.clearFocus()
                            scope.launch {
                                isLoading = true
                                error = null
                                try {
                                    val result = foodSearchRepository.searchFoods(searchQuery)
                                    searchResults = result.getOrDefault(emptyList())
                                    if (result.isFailure) {
                                        error = result.exceptionOrNull()?.message ?: "Search failed"
                                    }
                                } catch (e: Exception) {
                                    error = e.message ?: "Search failed"
                                }
                                isLoading = false
                                hasSearched = true
                            }
                        }
                    }
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selected food with quantity input
            if (selectedFood != null) {
                SelectedFoodCard(
                    food = selectedFood!!,
                    quantity = quantity,
                    onQuantityChange = { quantity = it },
                    onClear = {
                        selectedFood = null
                        quantity = "1"
                    },
                    onAdd = {
                        val qty = quantity.toDoubleOrNull() ?: 1.0
                        if (qty > 0) {
                            val ingredient = selectedFood!!.toRecipeIngredient(qty)
                            onIngredientSelected(ingredient)
                        }
                    }
                )
            } else {
                // Search results
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }

                        error != null -> {
                            Text(
                                text = error ?: "An error occurred",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(32.dp)
                            )
                        }

                        hasSearched && searchResults.isEmpty() -> {
                            Text(
                                text = "No results found for \"$searchQuery\"",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(32.dp)
                            )
                        }

                        !hasSearched -> {
                            Text(
                                text = "Type at least 2 characters and press search",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .padding(32.dp)
                            )
                        }

                        else -> {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(
                                    items = searchResults,
                                    key = { it.foodId }
                                ) { food ->
                                    FoodSearchItem(
                                        food = food,
                                        onClick = { selectedFood = food }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card showing the selected food with quantity input.
 */
@Composable
private fun SelectedFoodCard(
    food: SearchedFood,
    quantity: String,
    onQuantityChange: (String) -> Unit,
    onClear: () -> Unit,
    onAdd: () -> Unit
) {
    val defaultMeasure = food.defaultMeasure
    val unit = defaultMeasure?.label ?: "serving"
    val parsedQty = quantity.toDoubleOrNull() ?: 1.0
    val nutrition = defaultMeasure?.let { food.calculateNutrition(it, parsedQty) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = food.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (food.brand != null) {
                        Text(
                            text = food.brand,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(onClick = onClear) {
                    Text("Change")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity input
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = onQuantityChange,
                    modifier = Modifier.width(100.dp),
                    label = { Text("Qty") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )

                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Nutrition preview
            if (nutrition != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NutritionBadge(label = "Cal", value = "${nutrition.calories}", color = MaterialTheme.colorScheme.primary)
                    NutritionBadge(label = "P", value = "${nutrition.protein.toInt()}g", color = MaterialTheme.colorScheme.secondary)
                    NutritionBadge(label = "C", value = "${nutrition.carbs.toInt()}g", color = MaterialTheme.colorScheme.tertiary)
                    NutritionBadge(label = "F", value = "${nutrition.fat.toInt()}g", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth(),
                enabled = (quantity.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Add Ingredient")
            }
        }
    }
}

/**
 * Simple food item in search results.
 */
@Composable
private fun FoodSearchItem(
    food: SearchedFood,
    onClick: () -> Unit
) {
    val defaultMeasure = food.defaultMeasure
    val caloriesText = if (defaultMeasure != null) {
        "${food.calculateNutrition(defaultMeasure, 1.0).calories} cal per ${defaultMeasure.label}"
    } else {
        "${food.caloriesPer100g.toInt()} cal/100g"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (food.brand != null) {
                    Text(
                        text = food.brand,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = caloriesText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Convert SearchedFood to RecipeIngredient.
 */
private fun SearchedFood.toRecipeIngredient(quantity: Double): RecipeIngredient {
    val measure = defaultMeasure
    val nutrition = measure?.let { calculateNutrition(it, 1.0) }

    val simpleFoodItem = SimpleFoodItem(
        name = name,
        brand = brand,
        barcode = null,
        calories = nutrition?.calories?.toDouble() ?: caloriesPer100g,
        protein = nutrition?.protein ?: proteinPer100g,
        carbs = nutrition?.carbs ?: carbsPer100g,
        fat = nutrition?.fat ?: fatPer100g,
        servingSize = 1.0,
        servingUnit = measure?.label ?: "serving"
    )

    return RecipeIngredient(
        foodItem = simpleFoodItem,
        quantity = quantity
    )
}

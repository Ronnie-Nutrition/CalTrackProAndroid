package com.easyaiflows.caltrackpro.ui.entry

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualEntryScreen(
    onNavigateBack: () -> Unit,
    viewModel: ManualEntryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    // Handle save success
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onNavigateBack()
        }
    }

    // Show error in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) stringResource(R.string.manual_entry_title_edit) else stringResource(R.string.manual_entry_title_add)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Meal type selector
            MealTypeSelector(
                selectedMealType = uiState.mealType,
                onMealTypeSelected = viewModel::updateMealType
            )

            HorizontalDivider()

            // Food name
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::updateName,
                label = { Text(stringResource(R.string.manual_entry_food_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                ),
                isError = uiState.name.isBlank() && uiState.error != null
            )

            // Brand (optional)
            OutlinedTextField(
                value = uiState.brand,
                onValueChange = viewModel::updateBrand,
                label = { Text(stringResource(R.string.manual_entry_brand)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    imeAction = ImeAction.Next
                )
            )

            HorizontalDivider()

            // Serving info
            Text(
                text = stringResource(R.string.manual_entry_serving_info),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Serving size
                OutlinedTextField(
                    value = uiState.servingSize,
                    onValueChange = viewModel::updateServingSize,
                    label = { Text(stringResource(R.string.manual_entry_serving_size)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    isError = (uiState.servingSize.toDoubleOrNull() ?: 0.0) <= 0 && uiState.error != null
                )

                // Serving unit picker
                ServingUnitPicker(
                    selectedUnit = uiState.servingUnit,
                    onUnitSelected = viewModel::updateServingUnit,
                    modifier = Modifier.weight(1f)
                )
            }

            // Quantity
            OutlinedTextField(
                value = uiState.quantity,
                onValueChange = viewModel::updateQuantity,
                label = { Text(stringResource(R.string.manual_entry_num_servings)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                isError = (uiState.quantity.toDoubleOrNull() ?: 0.0) <= 0 && uiState.error != null
            )

            HorizontalDivider()

            // Nutrition info
            Text(
                text = stringResource(R.string.manual_entry_nutrition_info),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Calories
            OutlinedTextField(
                value = uiState.calories,
                onValueChange = viewModel::updateCalories,
                label = { Text(stringResource(R.string.nutrient_calories) + " *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                suffix = { Text(stringResource(R.string.unit_kcal)) },
                isError = uiState.calories.toDoubleOrNull() == null && uiState.error != null
            )

            // Macros row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.protein,
                    onValueChange = viewModel::updateProtein,
                    label = { Text(stringResource(R.string.nutrient_protein) + " *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    suffix = { Text(stringResource(R.string.unit_grams)) },
                    isError = uiState.protein.toDoubleOrNull() == null && uiState.error != null
                )

                OutlinedTextField(
                    value = uiState.carbs,
                    onValueChange = viewModel::updateCarbs,
                    label = { Text(stringResource(R.string.nutrient_carbs) + " *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    suffix = { Text(stringResource(R.string.unit_grams)) },
                    isError = uiState.carbs.toDoubleOrNull() == null && uiState.error != null
                )

                OutlinedTextField(
                    value = uiState.fat,
                    onValueChange = viewModel::updateFat,
                    label = { Text(stringResource(R.string.nutrient_fat) + " *") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    suffix = { Text(stringResource(R.string.unit_grams)) },
                    isError = uiState.fat.toDoubleOrNull() == null && uiState.error != null
                )
            }

            // Optional nutrients
            Text(
                text = stringResource(R.string.manual_entry_additional_nutrients),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = uiState.fiber,
                    onValueChange = viewModel::updateFiber,
                    label = { Text(stringResource(R.string.nutrient_fiber)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    suffix = { Text(stringResource(R.string.unit_grams)) }
                )

                OutlinedTextField(
                    value = uiState.sugar,
                    onValueChange = viewModel::updateSugar,
                    label = { Text(stringResource(R.string.nutrient_sugar)) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal,
                        imeAction = ImeAction.Next
                    ),
                    suffix = { Text(stringResource(R.string.unit_grams)) }
                )
            }

            OutlinedTextField(
                value = uiState.sodium,
                onValueChange = viewModel::updateSodium,
                label = { Text(stringResource(R.string.nutrient_sodium)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                suffix = { Text(stringResource(R.string.unit_milligrams)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Save button
            Button(
                onClick = viewModel::save,
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isValid && !uiState.isSaving
            ) {
                Text(
                    if (uiState.isSaving) stringResource(R.string.manual_entry_saving) else
                        if (uiState.isEditMode) stringResource(R.string.manual_entry_update) else stringResource(R.string.manual_entry_add)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MealTypeSelector(
    selectedMealType: MealType,
    onMealTypeSelected: (MealType) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        MealType.entries.forEachIndexed { index, mealType ->
            SegmentedButton(
                selected = mealType == selectedMealType,
                onClick = { onMealTypeSelected(mealType) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = MealType.entries.size
                ),
                label = { Text(mealType.getDisplayName()) }
            )
        }
    }
}

@Composable
private fun MealType.getDisplayName(): String {
    return when (this) {
        MealType.BREAKFAST -> stringResource(R.string.meal_breakfast)
        MealType.LUNCH -> stringResource(R.string.meal_lunch)
        MealType.DINNER -> stringResource(R.string.meal_dinner)
        MealType.SNACK -> stringResource(R.string.meal_snacks)
    }
}

@Composable
private fun ServingUnitPicker(
    selectedUnit: ServingUnit,
    onUnitSelected: (ServingUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedUnit.displayName)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            ServingUnit.entries.forEach { unit ->
                DropdownMenuItem(
                    text = { Text("${unit.displayName} (${unit.abbreviation})") },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}


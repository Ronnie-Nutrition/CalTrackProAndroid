package com.easyaiflows.caltrackpro.ui.diary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyaiflows.caltrackpro.domain.model.FoodEntry
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.ui.diary.components.AddFoodOptionsSheet
import com.easyaiflows.caltrackpro.ui.diary.components.DailySummaryCard
import com.easyaiflows.caltrackpro.ui.diary.components.DateSelector
import com.easyaiflows.caltrackpro.ui.diary.components.MealSection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onNavigateToManualEntry: (MealType) -> Unit,
    onNavigateToEditEntry: (String) -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showAddFoodSheet by remember { mutableStateOf(false) }
    var selectedMealType by remember { mutableStateOf<MealType?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf<FoodEntry?>(null) }

    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Diary") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedMealType = null
                    showAddFoodSheet = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add food"
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Date selector
                item {
                    DateSelector(
                        selectedDate = uiState.selectedDate,
                        isToday = uiState.isToday,
                        onPreviousDay = viewModel::previousDay,
                        onNextDay = viewModel::nextDay,
                        onDateSelected = viewModel::selectDate
                    )
                }

                // Daily summary card
                item {
                    DailySummaryCard(
                        totalCalories = uiState.totalCalories,
                        totalProtein = uiState.totalProtein,
                        totalCarbs = uiState.totalCarbs,
                        totalFat = uiState.totalFat,
                        caloriesProgress = uiState.caloriesProgress,
                        proteinProgress = uiState.proteinProgress,
                        carbsProgress = uiState.carbsProgress,
                        fatProgress = uiState.fatProgress,
                        goals = uiState.goals
                    )
                }

                // Meal sections
                items(MealType.entries.toList()) { mealType ->
                    val entries = uiState.entriesByMeal[mealType] ?: emptyList()
                    MealSection(
                        mealType = mealType,
                        entries = entries,
                        totalCalories = uiState.caloriesForMeal(mealType),
                        onAddClick = {
                            selectedMealType = mealType
                            showAddFoodSheet = true
                        },
                        onEntryClick = { entry ->
                            onNavigateToEditEntry(entry.id)
                        },
                        onEditEntry = { entry ->
                            onNavigateToEditEntry(entry.id)
                        },
                        onDuplicateEntry = { entry ->
                            viewModel.duplicateEntry(entry)
                            scope.launch {
                                snackbarHostState.showSnackbar("Entry duplicated")
                            }
                        },
                        onDeleteEntry = { entry ->
                            showDeleteConfirmation = entry
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }

    // Add food options bottom sheet
    if (showAddFoodSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddFoodSheet = false },
            sheetState = bottomSheetState
        ) {
            AddFoodOptionsSheet(
                mealType = selectedMealType,
                onSearchClick = {
                    showAddFoodSheet = false
                    // TODO: Navigate to search screen
                },
                onScanClick = {
                    showAddFoodSheet = false
                    // TODO: Navigate to barcode scanner
                },
                onManualClick = {
                    showAddFoodSheet = false
                    onNavigateToManualEntry(selectedMealType ?: MealType.SNACK)
                },
                onDismiss = { showAddFoodSheet = false }
            )
        }
    }

    // Delete confirmation dialog
    showDeleteConfirmation?.let { entry ->
        DeleteConfirmationDialog(
            entryName = entry.name,
            onConfirm = {
                viewModel.deleteEntry(entry.id)
                showDeleteConfirmation = null
                scope.launch {
                    snackbarHostState.showSnackbar("Entry deleted")
                }
            },
            onDismiss = { showDeleteConfirmation = null }
        )
    }
}

@Composable
private fun DeleteConfirmationDialog(
    entryName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Entry") },
        text = { Text("Are you sure you want to delete \"$entryName\"?") },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = onConfirm
            ) {
                Text(
                    "Delete",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

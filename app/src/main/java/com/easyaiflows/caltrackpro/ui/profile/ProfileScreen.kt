package com.easyaiflows.caltrackpro.ui.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easyaiflows.caltrackpro.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Pre-capture string for coroutine use
    val changesSavedMessage = stringResource(R.string.profile_changes_saved)

    // Show save success/error
    LaunchedEffect(uiState.saveSuccess, uiState.saveError) {
        when {
            uiState.saveSuccess -> snackbarHostState.showSnackbar(changesSavedMessage)
            uiState.saveError != null -> {
                snackbarHostState.showSnackbar(uiState.saveError!!)
                viewModel.clearError()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.profile_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.action_back)
                        )
                    }
                },
                actions = {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(end = 16.dp),
                            strokeWidth = 2.dp
                        )
                    } else if (uiState.saveSuccess) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Calorie Summary Card
                CalorieSummaryCard(
                    effectiveCalories = uiState.effectiveCalories,
                    calculatedCalories = uiState.calculatedCalories,
                    hasOverride = uiState.calorieOverride != null,
                    protein = uiState.calculatedProtein,
                    carbs = uiState.calculatedCarbs,
                    fat = uiState.calculatedFat
                )

                // Personal Info Section
                ExpandableSection(
                    title = stringResource(R.string.profile_section_personal),
                    expanded = uiState.personalInfoExpanded,
                    onToggle = viewModel::togglePersonalInfoExpanded
                ) {
                    PersonalInfoContent(
                        age = uiState.age,
                        sex = uiState.sex,
                        ageError = uiState.ageError,
                        onAgeChange = viewModel::updateAge,
                        onSexChange = viewModel::updateSex
                    )
                }

                // Body Metrics Section
                ExpandableSection(
                    title = stringResource(R.string.profile_section_body),
                    expanded = uiState.bodyMetricsExpanded,
                    onToggle = viewModel::toggleBodyMetricsExpanded
                ) {
                    BodyMetricsContent(
                        weightKg = uiState.weightKg,
                        heightCm = uiState.heightCm,
                        unitSystem = uiState.unitSystem,
                        weightError = uiState.weightError,
                        heightError = uiState.heightError,
                        onWeightChange = viewModel::updateWeight,
                        onHeightChange = viewModel::updateHeight,
                        onHeightFeetInchesChange = viewModel::updateHeightFeetInches,
                        onUnitSystemChange = viewModel::updateUnitSystem
                    )
                }

                // Goals Section
                ExpandableSection(
                    title = stringResource(R.string.profile_section_goals),
                    expanded = uiState.goalsExpanded,
                    onToggle = viewModel::toggleGoalsExpanded
                ) {
                    GoalsContent(
                        activityLevel = uiState.activityLevel,
                        weightGoal = uiState.weightGoal,
                        macroPreset = uiState.macroPreset,
                        calorieOverride = uiState.calorieOverride,
                        calculatedCalories = uiState.calculatedCalories,
                        onActivityLevelChange = viewModel::updateActivityLevel,
                        onWeightGoalChange = viewModel::updateWeightGoal,
                        onMacroPresetChange = viewModel::updateMacroPreset,
                        onCalorieOverrideChange = viewModel::setCalorieOverride,
                        onResetToCalculated = viewModel::resetToCalculated
                    )
                }

                // Preferences Section
                ExpandableSection(
                    title = stringResource(R.string.profile_section_preferences),
                    expanded = uiState.preferencesExpanded,
                    onToggle = viewModel::togglePreferencesExpanded
                ) {
                    PreferencesContent(
                        unitSystem = uiState.unitSystem,
                        onUnitSystemChange = viewModel::updateUnitSystem
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CalorieSummaryCard(
    effectiveCalories: Int,
    calculatedCalories: Int,
    hasOverride: Boolean,
    protein: Int,
    carbs: Int,
    fat: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.profile_daily_target),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$effectiveCalories",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.unit_calories),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (hasOverride) {
                Text(
                    text = stringResource(R.string.profile_custom_override, calculatedCalories),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroChip(stringResource(R.string.nutrient_protein), "${protein}g")
                MacroChip(stringResource(R.string.nutrient_carbs), "${carbs}g")
                MacroChip(stringResource(R.string.nutrient_fat), "${fat}g")
            }
        }
    }
}

@Composable
private fun MacroChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun ExpandableSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) stringResource(R.string.profile_collapse) else stringResource(R.string.profile_expand)
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoContent(
    age: Int,
    sex: Sex,
    ageError: String?,
    onAgeChange: (Int) -> Unit,
    onSexChange: (Sex) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = age.toString(),
            onValueChange = { it.toIntOrNull()?.let(onAgeChange) },
            label = { Text(stringResource(R.string.profile_age)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = ageError != null,
            supportingText = ageError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Text(stringResource(R.string.profile_sex), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Sex.entries.forEachIndexed { index, sexOption ->
                SegmentedButton(
                    selected = sex == sexOption,
                    onClick = { onSexChange(sexOption) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = Sex.entries.size)
                ) {
                    Text(sexOption.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
    }
}

@Composable
private fun BodyMetricsContent(
    weightKg: Double,
    heightCm: Double,
    unitSystem: UnitSystem,
    weightError: String?,
    heightError: String?,
    onWeightChange: (Double) -> Unit,
    onHeightChange: (Double) -> Unit,
    onHeightFeetInchesChange: (Int, Int) -> Unit,
    onUnitSystemChange: (UnitSystem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        val displayWeight = when (unitSystem) {
            UnitSystem.METRIC -> weightKg
            UnitSystem.IMPERIAL -> UnitSystem.kgToLbs(weightKg)
        }

        OutlinedTextField(
            value = String.format("%.1f", displayWeight),
            onValueChange = { value ->
                value.toDoubleOrNull()?.let { weight ->
                    val kg = when (unitSystem) {
                        UnitSystem.METRIC -> weight
                        UnitSystem.IMPERIAL -> UnitSystem.lbsToKg(weight)
                    }
                    onWeightChange(kg)
                }
            },
            label = { Text(stringResource(R.string.profile_weight, unitSystem.weightUnit)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = weightError != null,
            supportingText = weightError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        when (unitSystem) {
            UnitSystem.METRIC -> {
                OutlinedTextField(
                    value = heightCm.roundToInt().toString(),
                    onValueChange = { it.toDoubleOrNull()?.let(onHeightChange) },
                    label = { Text(stringResource(R.string.profile_height_cm)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = heightError != null,
                    supportingText = heightError?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            UnitSystem.IMPERIAL -> {
                val (feet, inches) = UnitSystem.cmToFeetInches(heightCm)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = feet.toString(),
                        onValueChange = { ft ->
                            ft.toIntOrNull()?.let { onHeightFeetInchesChange(it, inches) }
                        },
                        label = { Text(stringResource(R.string.profile_height_feet)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = inches.toString(),
                        onValueChange = { inch ->
                            inch.toIntOrNull()?.let { onHeightFeetInchesChange(feet, it) }
                        },
                        label = { Text(stringResource(R.string.profile_height_inches)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = heightError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GoalsContent(
    activityLevel: ActivityLevel,
    weightGoal: WeightGoal,
    macroPreset: MacroPreset,
    calorieOverride: Int?,
    calculatedCalories: Int,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    onWeightGoalChange: (WeightGoal) -> Unit,
    onMacroPresetChange: (MacroPreset) -> Unit,
    onCalorieOverrideChange: (Int?) -> Unit,
    onResetToCalculated: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Activity Level
        Text(stringResource(R.string.profile_activity_level), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ActivityLevel.entries.forEachIndexed { index, level ->
                SegmentedButton(
                    selected = activityLevel == level,
                    onClick = { onActivityLevelChange(level) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = ActivityLevel.entries.size)
                ) {
                    Text(level.displayName.take(6))
                }
            }
        }

        // Weight Goal
        Text(stringResource(R.string.profile_weight_goal), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            WeightGoal.entries.forEachIndexed { index, goal ->
                SegmentedButton(
                    selected = weightGoal == goal,
                    onClick = { onWeightGoalChange(goal) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = WeightGoal.entries.size)
                ) {
                    Text(goal.displayName.take(8))
                }
            }
        }

        // Macro Preset
        Text(stringResource(R.string.profile_diet_style), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            MacroPreset.entries.filter { it != MacroPreset.CUSTOM }.forEachIndexed { index, preset ->
                SegmentedButton(
                    selected = macroPreset == preset,
                    onClick = { onMacroPresetChange(preset) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 3)
                ) {
                    Text(preset.displayName.take(8))
                }
            }
        }

        // Calorie Override
        Text(stringResource(R.string.profile_calorie_target), style = MaterialTheme.typography.labelLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = (calorieOverride ?: calculatedCalories).toString(),
                onValueChange = { it.toIntOrNull()?.let(onCalorieOverrideChange) },
                label = { Text(stringResource(R.string.profile_daily_calories)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            if (calorieOverride != null) {
                TextButton(onClick = onResetToCalculated) {
                    Text(stringResource(R.string.action_reset))
                }
            }
        }
        if (calorieOverride == null) {
            Text(
                text = stringResource(R.string.profile_calculated_info),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PreferencesContent(
    unitSystem: UnitSystem,
    onUnitSystemChange: (UnitSystem) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(stringResource(R.string.profile_unit_system), style = MaterialTheme.typography.labelLarge)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UnitSystem.entries.forEachIndexed { index, system ->
                SegmentedButton(
                    selected = unitSystem == system,
                    onClick = { onUnitSystemChange(system) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = UnitSystem.entries.size)
                ) {
                    Text(system.displayName)
                }
            }
        }
    }
}

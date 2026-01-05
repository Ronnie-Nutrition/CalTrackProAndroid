package com.easyaiflows.caltrackpro.ui.onboarding.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.domain.model.ActivityLevel
import com.easyaiflows.caltrackpro.domain.model.MacroPreset
import com.easyaiflows.caltrackpro.domain.model.Sex
import com.easyaiflows.caltrackpro.domain.model.UnitSystem
import com.easyaiflows.caltrackpro.domain.model.WeightGoal
import com.easyaiflows.caltrackpro.ui.onboarding.OnboardingUiState
import kotlin.math.roundToInt

// Task 4.3: Welcome Page
@Composable
fun WelcomePage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "CalTrackPro",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Track your nutrition, reach your goals",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Let's set up your personalized nutrition plan",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Task 4.4: Personal Info Page
@Composable
fun PersonalInfoPage(
    age: Int,
    sex: Sex,
    ageError: String?,
    onAgeChange: (Int) -> Unit,
    onSexChange: (Sex) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "About You",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "This helps us calculate your daily needs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Age input
        Text(
            text = "Age",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = if (age > 0) age.toString() else "",
            onValueChange = { value ->
                value.toIntOrNull()?.let { onAgeChange(it) }
            },
            label = { Text("Years") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = ageError != null,
            supportingText = ageError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sex selection
        Text(
            text = "Biological Sex",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            Sex.entries.forEachIndexed { index, sexOption ->
                SegmentedButton(
                    selected = sex == sexOption,
                    onClick = { onSexChange(sexOption) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = Sex.entries.size
                    )
                ) {
                    Text(sexOption.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Used for accurate calorie calculations",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Task 4.5: Body Metrics Page
@Composable
fun BodyMetricsPage(
    weightKg: Double,
    heightCm: Double,
    unitSystem: UnitSystem,
    weightError: String?,
    heightError: String?,
    onWeightChange: (Double) -> Unit,
    onHeightChange: (Double) -> Unit,
    onHeightFeetInchesChange: (Int, Int) -> Unit,
    onUnitSystemChange: (UnitSystem) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Body Metrics",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Used to calculate your calorie needs",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Unit system toggle
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            UnitSystem.entries.forEachIndexed { index, system ->
                SegmentedButton(
                    selected = unitSystem == system,
                    onClick = { onUnitSystemChange(system) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = UnitSystem.entries.size
                    )
                ) {
                    Text(system.displayName)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weight input
        val displayWeight = when (unitSystem) {
            UnitSystem.METRIC -> weightKg
            UnitSystem.IMPERIAL -> UnitSystem.kgToLbs(weightKg)
        }
        OutlinedTextField(
            value = String.format("%.1f", displayWeight),
            onValueChange = { value ->
                value.toDoubleOrNull()?.let { weight ->
                    val weightInKg = when (unitSystem) {
                        UnitSystem.METRIC -> weight
                        UnitSystem.IMPERIAL -> UnitSystem.lbsToKg(weight)
                    }
                    onWeightChange(weightInKg)
                }
            },
            label = { Text("Weight (${unitSystem.weightUnit})") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            isError = weightError != null,
            supportingText = weightError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Height input
        when (unitSystem) {
            UnitSystem.METRIC -> {
                OutlinedTextField(
                    value = heightCm.roundToInt().toString(),
                    onValueChange = { value ->
                        value.toDoubleOrNull()?.let { onHeightChange(it) }
                    },
                    label = { Text("Height (cm)") },
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
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { ft ->
                                onHeightFeetInchesChange(ft, inches)
                            }
                        },
                        label = { Text("Feet") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = inches.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()?.let { inch ->
                                onHeightFeetInchesChange(feet, inch)
                            }
                        },
                        label = { Text("Inches") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = heightError != null,
                        modifier = Modifier.weight(1f)
                    )
                }
                heightError?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

// Task 4.6: Activity Level Page
@Composable
fun ActivityLevelPage(
    activityLevel: ActivityLevel,
    onActivityLevelChange: (ActivityLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Activity Level",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "How active are you on a typical day?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        ActivityLevel.entries.forEach { level ->
            SelectableCard(
                title = level.displayName,
                description = level.description,
                selected = activityLevel == level,
                onClick = { onActivityLevelChange(level) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Task 4.7: Weight Goal Page
@Composable
fun WeightGoalPage(
    weightGoal: WeightGoal,
    calculatedTDEE: Double,
    onWeightGoalChange: (WeightGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Goal",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "What do you want to achieve?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        WeightGoal.entries.forEach { goal ->
            val caloriePreview = (calculatedTDEE + goal.calorieAdjustment).roundToInt()
            SelectableCard(
                title = goal.displayName,
                description = "${goal.description} (~$caloriePreview cal/day)",
                selected = weightGoal == goal,
                onClick = { onWeightGoalChange(goal) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Task 4.8: Macro Preset Page
@Composable
fun MacroPresetPage(
    macroPreset: MacroPreset,
    onMacroPresetChange: (MacroPreset) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Diet Style",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose your macro distribution",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        MacroPreset.entries.filter { it != MacroPreset.CUSTOM }.forEach { preset ->
            val macroText = "P: ${preset.proteinPercent}% | C: ${preset.carbsPercent}% | F: ${preset.fatPercent}%"
            SelectableCard(
                title = preset.displayName,
                description = "${preset.description}\n$macroText",
                selected = macroPreset == preset,
                onClick = { onMacroPresetChange(preset) },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

// Task 4.9: Review Page
@Composable
fun ReviewPage(
    state: OnboardingUiState,
    onCalorieOverrideChange: (Int?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Your Plan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Review your personalized targets",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Calorie target
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
                    text = "Daily Calories",
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "${state.effectiveCalories}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (state.calorieOverride != null) {
                    Text(
                        text = "(Custom override)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Macro breakdown
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MacroCard(label = "Protein", grams = state.calculatedProtein, color = "green")
            MacroCard(label = "Carbs", grams = state.calculatedCarbs, color = "blue")
            MacroCard(label = "Fat", grams = state.calculatedFat, color = "orange")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Summary
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Summary", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                SummaryRow("Age", "${state.age} years")
                SummaryRow("Sex", state.sex.name.lowercase().replaceFirstChar { it.uppercase() })
                SummaryRow("Weight", UnitSystem.formatWeight(state.weightKg, state.unitSystem))
                SummaryRow("Height", UnitSystem.formatHeight(state.heightCm, state.unitSystem))
                SummaryRow("Activity", state.activityLevel.displayName)
                SummaryRow("Goal", state.weightGoal.displayName)
                SummaryRow("Diet Style", state.macroPreset.displayName)
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun MacroCard(label: String, grams: Int, color: String) {
    Card(
        modifier = Modifier.width(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, style = MaterialTheme.typography.labelMedium)
            Text(
                text = "${grams}g",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Task 4.10: Completion Page
@Composable
fun CompletionPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Complete",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "You're All Set!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your personalized nutrition plan is ready",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(48.dp))
        Text(
            text = "Tap 'Start Tracking' to begin your journey",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Shared Components

@Composable
fun SelectableCard(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick)
            .then(
                if (selected) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

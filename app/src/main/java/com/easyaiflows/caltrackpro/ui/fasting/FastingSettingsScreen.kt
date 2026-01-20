package com.easyaiflows.caltrackpro.ui.fasting

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingSettingsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: FastingSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fasting_settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.action_back))
                    }
                }
            )
        }
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
                // Schedule Settings
                SettingsSection(title = stringResource(R.string.fasting_settings_schedule)) {
                    ScheduleSettingItem(
                        selectedSchedule = uiState.selectedSchedule,
                        onScheduleSelected = viewModel::setSelectedSchedule
                    )

                    if (uiState.selectedSchedule == FastingSchedule.CUSTOM) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CustomHoursSettingItem(
                            hours = uiState.customFastingHours,
                            onHoursChanged = viewModel::setCustomFastingHours
                        )
                    }
                }

                // Water Settings
                SettingsSection(title = stringResource(R.string.fasting_settings_water_tracking)) {
                    WaterGoalSettingItem(
                        glasses = uiState.waterGoalGlasses,
                        onGlassesChanged = viewModel::setWaterGoalGlasses
                    )
                }

                // Notification Settings
                SettingsSection(title = stringResource(R.string.fasting_settings_notifications)) {
                    SwitchSettingItem(
                        title = stringResource(R.string.fasting_settings_enable_all),
                        description = stringResource(R.string.fasting_settings_enable_all_desc),
                        checked = uiState.remindersEnabled,
                        onCheckedChange = viewModel::setRemindersEnabled
                    )

                    if (uiState.remindersEnabled) {
                        Spacer(modifier = Modifier.height(8.dp))

                        SwitchSettingItem(
                            title = stringResource(R.string.fasting_settings_complete),
                            description = stringResource(R.string.fasting_settings_complete_desc),
                            checked = uiState.fastingCompleteNotification,
                            onCheckedChange = viewModel::setFastingCompleteNotification
                        )

                        SwitchSettingItem(
                            title = stringResource(R.string.fasting_settings_milestone),
                            description = stringResource(R.string.fasting_settings_milestone_desc),
                            checked = uiState.milestoneNotifications,
                            onCheckedChange = viewModel::setMilestoneNotifications
                        )

                        SwitchSettingItem(
                            title = stringResource(R.string.fasting_settings_water),
                            description = stringResource(R.string.fasting_settings_water_desc),
                            checked = uiState.waterReminderNotifications,
                            onCheckedChange = viewModel::setWaterReminderNotifications
                        )

                        SwitchSettingItem(
                            title = stringResource(R.string.fasting_settings_eating_alerts),
                            description = stringResource(R.string.fasting_settings_eating_alerts_desc),
                            checked = uiState.eatingWindowNotifications,
                            onCheckedChange = viewModel::setEatingWindowNotifications
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleSettingItem(
    selectedSchedule: FastingSchedule,
    onScheduleSelected: (FastingSchedule) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(
            text = stringResource(R.string.fasting_settings_default_schedule),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(8.dp))

        val scheduleText = if (selectedSchedule == FastingSchedule.CUSTOM) {
            stringResource(R.string.fasting_settings_custom_hours, selectedSchedule.displayName)
        } else {
            "${selectedSchedule.displayName} - ${stringResource(R.string.fasting_settings_hours_fasting, selectedSchedule.fastingHours)}"
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = scheduleText,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                FastingSchedule.entries.forEach { schedule ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(
                                    text = schedule.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                if (schedule != FastingSchedule.CUSTOM) {
                                    Text(
                                        text = stringResource(
                                            R.string.fasting_schedule_description,
                                            schedule.fastingHours,
                                            schedule.eatingHours
                                        ),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        },
                        onClick = {
                            onScheduleSelected(schedule)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomHoursSettingItem(
    hours: Int,
    onHoursChanged: (Int) -> Unit
) {
    var textValue by remember(hours) { mutableStateOf(hours.toString()) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.fasting_settings_custom_hours_label),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = stringResource(R.string.fasting_settings_eating_window, 24 - hours),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                val filtered = newValue.filter { it.isDigit() }
                if (filtered.length <= 2) {
                    textValue = filtered
                    filtered.toIntOrNull()?.let { h ->
                        if (h in 1..23) onHoursChanged(h)
                    }
                }
            },
            modifier = Modifier.width(80.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
    }
}

@Composable
private fun WaterGoalSettingItem(
    glasses: Int,
    onGlassesChanged: (Int) -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.fasting_settings_water_goal),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = stringResource(R.string.fasting_settings_glasses_count, glasses),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Slider(
            value = glasses.toFloat(),
            onValueChange = { onGlassesChanged(it.toInt()) },
            valueRange = 1f..20f,
            steps = 18,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.fasting_settings_water_recommendation),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SwitchSettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

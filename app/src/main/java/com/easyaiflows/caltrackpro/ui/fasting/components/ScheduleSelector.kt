package com.easyaiflows.caltrackpro.ui.fasting.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule

/**
 * Dropdown selector for fasting schedule with custom hours input.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSelector(
    selectedSchedule: FastingSchedule,
    customFastingHours: Int,
    onScheduleSelected: (FastingSchedule) -> Unit,
    onCustomHoursChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var customHoursText by remember(customFastingHours) { mutableStateOf(customFastingHours.toString()) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Fasting Schedule",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Schedule dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = getScheduleDisplayText(selectedSchedule, customFastingHours),
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
                                            text = "${schedule.fastingHours}h fasting, ${schedule.eatingHours}h eating",
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

            // Custom hours input (visible when CUSTOM selected)
            AnimatedVisibility(
                visible = selectedSchedule == FastingSchedule.CUSTOM,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = customHoursText,
                            onValueChange = { newValue ->
                                // Only allow digits
                                val filtered = newValue.filter { it.isDigit() }
                                if (filtered.length <= 2) {
                                    customHoursText = filtered
                                    filtered.toIntOrNull()?.let { hours ->
                                        if (hours in 1..23) {
                                            onCustomHoursChanged(hours)
                                        }
                                    }
                                }
                            },
                            label = { Text("Fasting hours") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(120.dp),
                            shape = RoundedCornerShape(12.dp),
                            isError = customHoursText.toIntOrNull()?.let { it !in 1..23 } ?: false,
                            supportingText = {
                                if (customHoursText.toIntOrNull()?.let { it !in 1..23 } == true) {
                                    Text("Enter 1-23")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "hours fasting, ${24 - (customHoursText.toIntOrNull() ?: customFastingHours)} hours eating",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

private fun getScheduleDisplayText(schedule: FastingSchedule, customHours: Int): String {
    return if (schedule == FastingSchedule.CUSTOM) {
        "Custom ($customHours:${24 - customHours})"
    } else {
        "${schedule.displayName} - ${schedule.fastingHours}h fasting"
    }
}

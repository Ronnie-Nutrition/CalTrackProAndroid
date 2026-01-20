package com.easyaiflows.caltrackpro.ui.fasting

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easyaiflows.caltrackpro.R
import com.easyaiflows.caltrackpro.ui.fasting.components.FastingProgressRing
import com.easyaiflows.caltrackpro.ui.fasting.components.FastingStatsCard
import com.easyaiflows.caltrackpro.ui.fasting.components.MilestoneCard
import com.easyaiflows.caltrackpro.ui.fasting.components.ScheduleSelector
import com.easyaiflows.caltrackpro.ui.fasting.components.WaterTracker
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FastingScreen(
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    viewModel: FastingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showStopDialog by remember { mutableStateOf(false) }
    var showBatteryOptimizationDialog by remember { mutableStateOf(false) }

    // Pre-capture strings for coroutine use
    val milestoneReachedTemplate = stringResource(R.string.fasting_milestone_reached)
    val fastingCompletedMessage = stringResource(R.string.fasting_completed)
    val eatingWindowClosingSoon = stringResource(R.string.fasting_eating_closing_soon)
    val eatingWindowClosed = stringResource(R.string.fasting_eating_closed)

    // Check if notification permission is needed (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, start fasting
            viewModel.startFasting()
        } else {
            // Permission denied, show snackbar
            viewModel.onPermissionDenied()
        }
    }

    // Function to start fasting with permission check
    val startFastingWithPermissionCheck: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Check notification permission for Android 13+
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // No permission needed for older Android versions
            viewModel.startFasting()
        }

        // Check battery optimization
        val powerManager = context.getSystemService(PowerManager::class.java)
        if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
            showBatteryOptimizationDialog = true
        }
    }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is FastingEvent.NavigateToHistory -> onNavigateToHistory()
                is FastingEvent.NavigateToSettings -> onNavigateToSettings()
                is FastingEvent.ShowMilestoneReached -> {
                    snackbarHostState.showSnackbar(milestoneReachedTemplate.format(event.milestoneTitle))
                }
                is FastingEvent.ShowFastingComplete -> {
                    snackbarHostState.showSnackbar(fastingCompletedMessage)
                }
                is FastingEvent.ShowEatingWindowWarning -> {
                    snackbarHostState.showSnackbar(eatingWindowClosingSoon)
                }
                is FastingEvent.ShowEatingWindowClosed -> {
                    snackbarHostState.showSnackbar(eatingWindowClosed)
                }
                is FastingEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
                is FastingEvent.TriggerHapticFeedback -> {
                    triggerHapticFeedback(context)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.fasting_title)) },
                actions = {
                    IconButton(onClick = { viewModel.navigateToHistory() }) {
                        Icon(Icons.Default.History, contentDescription = stringResource(R.string.fasting_history))
                    }
                    IconButton(onClick = { viewModel.navigateToSettings() }) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.fasting_settings))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is FastingUiState.Loading -> {
                    LoadingState()
                }
                is FastingUiState.NotStarted -> {
                    NotStartedState(
                        state = state,
                        onScheduleSelected = viewModel::selectSchedule,
                        onCustomHoursChanged = viewModel::setCustomFastingHours,
                        onStartFasting = startFastingWithPermissionCheck,
                        onIncrementWater = viewModel::incrementWater,
                        onDecrementWater = viewModel::decrementWater
                    )
                }
                is FastingUiState.Fasting -> {
                    FastingState(
                        state = state,
                        onStopFasting = { showStopDialog = true },
                        onIncrementWater = viewModel::incrementWater,
                        onDecrementWater = viewModel::decrementWater
                    )
                }
                is FastingUiState.Eating -> {
                    EatingState(
                        state = state,
                        onEndEatingWindow = viewModel::endEatingWindow,
                        onIncrementWater = viewModel::incrementWater,
                        onDecrementWater = viewModel::decrementWater
                    )
                }
                is FastingUiState.Error -> {
                    ErrorState(
                        message = state.message,
                        onRetry = { /* Could add retry logic */ }
                    )
                }
            }
        }
    }

    // Stop fasting confirmation dialog
    if (showStopDialog) {
        StopFastingDialog(
            onDismiss = { showStopDialog = false },
            onStopAndSave = {
                viewModel.stopFasting(saveSession = true)
                showStopDialog = false
            },
            onStopAndDiscard = {
                viewModel.stopFasting(saveSession = false)
                showStopDialog = false
            }
        )
    }

    // Battery optimization dialog
    if (showBatteryOptimizationDialog) {
        BatteryOptimizationDialog(
            onDismiss = { showBatteryOptimizationDialog = false },
            onOpenSettings = {
                showBatteryOptimizationDialog = false
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun NotStartedState(
    state: FastingUiState.NotStarted,
    onScheduleSelected: (com.easyaiflows.caltrackpro.domain.model.FastingSchedule) -> Unit,
    onCustomHoursChanged: (Int) -> Unit,
    onStartFasting: () -> Unit,
    onIncrementWater: () -> Unit,
    onDecrementWater: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stats card
        FastingStatsCard(stats = state.stats)

        Spacer(modifier = Modifier.height(24.dp))

        // Schedule selector
        ScheduleSelector(
            selectedSchedule = state.selectedSchedule,
            customFastingHours = state.customFastingHours,
            onScheduleSelected = onScheduleSelected,
            onCustomHoursChanged = onCustomHoursChanged
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Water tracker
        WaterTracker(
            waterIntake = 0,
            waterGoal = state.waterGoalGlasses,
            onIncrement = onIncrementWater,
            onDecrement = onDecrementWater
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Start fasting button
        Button(
            onClick = onStartFasting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8E44AD)
            )
        ) {
            Text(
                text = stringResource(R.string.fasting_start),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val fastingHours = if (state.selectedSchedule == com.easyaiflows.caltrackpro.domain.model.FastingSchedule.CUSTOM)
            state.customFastingHours
        else
            state.selectedSchedule.fastingHours

        Text(
            text = stringResource(R.string.fasting_info, state.selectedSchedule.displayName, fastingHours),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FastingState(
    state: FastingUiState.Fasting,
    onStopFasting: () -> Unit,
    onIncrementWater: () -> Unit,
    onDecrementWater: () -> Unit
) {
    val targetHours = state.targetDuration.toHours().toInt()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Progress ring
        FastingProgressRing(
            progress = state.progress,
            elapsedHours = state.elapsedHours,
            elapsedMinutes = state.elapsedMinutes,
            elapsedSeconds = state.elapsedSeconds,
            remainingHours = state.remainingHours,
            remainingMinutes = state.remainingMinutes,
            targetHours = targetHours,
            isGoalReached = state.isGoalReached
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Milestone card
        MilestoneCard(
            currentMilestone = state.currentMilestone,
            nextMilestone = state.nextMilestone,
            hoursUntilNextMilestone = state.hoursUntilNextMilestone
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Water tracker
        WaterTracker(
            waterIntake = state.waterIntake,
            waterGoal = state.waterGoalGlasses,
            onIncrement = onIncrementWater,
            onDecrement = onDecrementWater
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats card
        FastingStatsCard(stats = state.stats)

        Spacer(modifier = Modifier.height(24.dp))

        // Stop button
        OutlinedButton(
            onClick = onStopFasting,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(
                text = stringResource(R.string.fasting_stop),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EatingState(
    state: FastingUiState.Eating,
    onEndEatingWindow: () -> Unit,
    onIncrementWater: () -> Unit,
    onDecrementWater: () -> Unit
) {
    val greenColor = Color(0xFF27AE60)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Eating window indicator
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(greenColor.copy(alpha = 0.1f), RoundedCornerShape(100.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.fasting_eating_window),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = greenColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.fasting_time_remaining, state.remainingHours, state.remainingMinutes),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = stringResource(R.string.fasting_remaining),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Completed fast info
        Text(
            text = stringResource(
                R.string.fasting_completed_message,
                state.completedFastDuration.toHours().toInt(),
                (state.completedFastDuration.toMinutes() % 60).toInt()
            ),
            style = MaterialTheme.typography.titleMedium,
            color = greenColor,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Water tracker
        WaterTracker(
            waterIntake = state.waterIntake,
            waterGoal = state.waterGoalGlasses,
            onIncrement = onIncrementWater,
            onDecrement = onDecrementWater
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Stats card
        FastingStatsCard(stats = state.stats)

        Spacer(modifier = Modifier.height(32.dp))

        // End eating window button
        Button(
            onClick = onEndEatingWindow,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = greenColor
            )
        ) {
            Text(
                text = stringResource(R.string.fasting_end_eating_window),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.fasting_wait_for_close),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.fasting_error_title),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onRetry) {
                Text(stringResource(R.string.action_retry))
            }
        }
    }
}

@Composable
private fun StopFastingDialog(
    onDismiss: () -> Unit,
    onStopAndSave: () -> Unit,
    onStopAndDiscard: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.fasting_stop_title)) },
        text = {
            Text(stringResource(R.string.fasting_stop_message))
        },
        confirmButton = {
            TextButton(onClick = onStopAndSave) {
                Text(stringResource(R.string.fasting_save_stop))
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onStopAndDiscard) {
                    Text(stringResource(R.string.action_discard), color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        }
    )
}

private fun triggerHapticFeedback(context: android.content.Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(android.content.Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator?.vibrate(
            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(100)
        }
    }
}

@Composable
private fun BatteryOptimizationDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.fasting_battery_title)) },
        text = {
            Text(stringResource(R.string.fasting_battery_message))
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.action_open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_not_now))
            }
        }
    )
}

package com.easyaiflows.caltrackpro.ui.fasting

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FastingRepository
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingSession
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.domain.model.FastingStats
import com.easyaiflows.caltrackpro.domain.model.getCurrentMilestone
import com.easyaiflows.caltrackpro.domain.model.getNextMilestone
import com.easyaiflows.caltrackpro.service.FastingTimerService
import com.easyaiflows.caltrackpro.widget.FastingWidgetProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FastingViewModel @Inject constructor(
    private val repository: FastingRepository,
    private val application: Application
) : ViewModel() {

    companion object {
        private const val TAG = "FastingAnalytics"
    }

    private val _uiState = MutableStateFlow<FastingUiState>(FastingUiState.Loading)
    val uiState: StateFlow<FastingUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<FastingEvent>()
    val events: SharedFlow<FastingEvent> = _events.asSharedFlow()

    private var timerJob: Job? = null
    private var lastMilestoneHours: Int = 0

    init {
        observeFastingState()
    }

    private fun observeFastingState() {
        viewModelScope.launch {
            repository.fastingDataState.collect { dataState ->
                repository.resetWaterIfNewDay()

                val stats = repository.stats.first()

                when (dataState.currentState) {
                    FastingState.NOT_STARTED -> {
                        stopTimer()
                        _uiState.value = FastingUiState.NotStarted(
                            selectedSchedule = dataState.selectedSchedule,
                            customFastingHours = dataState.customFastingHours,
                            stats = stats,
                            waterGoalGlasses = dataState.waterGoalGlasses
                        )
                    }
                    FastingState.FASTING -> {
                        val startTime = dataState.fastingStartTime ?: Instant.now()
                        val targetHours = if (dataState.selectedSchedule == FastingSchedule.CUSTOM) {
                            dataState.customFastingHours
                        } else {
                            dataState.selectedSchedule.fastingHours
                        }
                        val targetDuration = Duration.ofHours(targetHours.toLong())

                        startTimer(startTime, targetDuration, dataState.selectedSchedule, stats, dataState.waterIntake, dataState.waterGoalGlasses)
                    }
                    FastingState.EATING -> {
                        val eatingStartTime = dataState.eatingWindowStartTime ?: Instant.now()
                        val fastingStartTime = dataState.fastingStartTime ?: eatingStartTime
                        val eatingHours = if (dataState.selectedSchedule == FastingSchedule.CUSTOM) {
                            24 - dataState.customFastingHours
                        } else {
                            dataState.selectedSchedule.eatingHours
                        }
                        val eatingWindowDuration = Duration.ofHours(eatingHours.toLong())
                        val completedFastDuration = Duration.between(fastingStartTime, eatingStartTime)

                        startEatingTimer(
                            eatingStartTime,
                            eatingWindowDuration,
                            completedFastDuration,
                            dataState.selectedSchedule,
                            stats,
                            dataState.waterIntake,
                            dataState.waterGoalGlasses
                        )
                    }
                }
            }
        }
    }

    private fun startTimer(
        startTime: Instant,
        targetDuration: Duration,
        schedule: FastingSchedule,
        stats: FastingStats,
        waterIntake: Int,
        waterGoalGlasses: Int
    ) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = Duration.between(startTime, Instant.now())
                val progress = (elapsed.toMillis().toFloat() / targetDuration.toMillis()).coerceIn(0f, 1f)
                val elapsedHours = elapsed.toHours().toInt()

                val currentMilestone = getCurrentMilestone(elapsedHours)
                val nextMilestone = getNextMilestone(elapsedHours)

                // Check for milestone reached
                if (elapsedHours > lastMilestoneHours && currentMilestone.hours > 0) {
                    lastMilestoneHours = elapsedHours
                    if (currentMilestone.hours == elapsedHours) {
                        // Analytics: milestone reached
                        Log.i(TAG, "milestone_reached: hours=${currentMilestone.hours}, title=${currentMilestone.title}")
                        _events.emit(FastingEvent.ShowMilestoneReached(currentMilestone.title))
                        _events.emit(FastingEvent.TriggerHapticFeedback)
                    }
                }

                // Check for goal reached - transition to eating
                if (elapsed >= targetDuration) {
                    transitionToEating(startTime, schedule)
                    return@launch
                }

                _uiState.value = FastingUiState.Fasting(
                    startTime = startTime,
                    targetDuration = targetDuration,
                    schedule = schedule,
                    elapsedDuration = elapsed,
                    progress = progress,
                    currentMilestone = currentMilestone,
                    nextMilestone = nextMilestone,
                    waterIntake = waterIntake,
                    waterGoalGlasses = waterGoalGlasses,
                    stats = stats
                )

                delay(1000) // Update every second
            }
        }
    }

    private fun startEatingTimer(
        eatingStartTime: Instant,
        eatingWindowDuration: Duration,
        completedFastDuration: Duration,
        schedule: FastingSchedule,
        stats: FastingStats,
        waterIntake: Int,
        waterGoalGlasses: Int
    ) {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var warningShown = false

            while (true) {
                val elapsed = Duration.between(eatingStartTime, Instant.now())
                val remaining = eatingWindowDuration.minus(elapsed)

                // Show warning 1 hour before closing
                if (!warningShown && remaining <= Duration.ofHours(1) && remaining > Duration.ZERO) {
                    _events.emit(FastingEvent.ShowEatingWindowWarning)
                    warningShown = true
                }

                // Check if eating window closed
                if (elapsed >= eatingWindowDuration) {
                    _events.emit(FastingEvent.ShowEatingWindowClosed)
                }

                _uiState.value = FastingUiState.Eating(
                    fastingEndTime = eatingStartTime,
                    eatingWindowStartTime = eatingStartTime,
                    eatingWindowDuration = eatingWindowDuration,
                    completedFastDuration = completedFastDuration,
                    schedule = schedule,
                    elapsedEatingDuration = elapsed,
                    waterIntake = waterIntake,
                    waterGoalGlasses = waterGoalGlasses,
                    stats = stats
                )

                delay(1000)
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    private suspend fun transitionToEating(fastingStartTime: Instant, schedule: FastingSchedule) {
        val now = Instant.now()
        val fastDuration = Duration.between(fastingStartTime, now)
        val targetHours = if (schedule == FastingSchedule.CUSTOM) {
            repository.fastingDataState.first().customFastingHours
        } else {
            schedule.fastingHours
        }

        // Save completed session
        val session = FastingSession(
            id = UUID.randomUUID().toString(),
            startTime = fastingStartTime,
            endTime = now,
            targetDuration = Duration.ofHours(targetHours.toLong()),
            schedule = schedule,
            completed = true
        )
        repository.saveSession(session)

        // Transition to eating state
        repository.saveFastingState(FastingState.EATING, fastingStartTime)
        repository.saveEatingWindowStartTime(now)

        // Stop the foreground service
        FastingTimerService.stopService(application)

        // Update widget
        FastingWidgetProvider.updateAllWidgets(application)

        // Analytics: fast completed
        Log.i(TAG, "fast_completed: duration_hours=${fastDuration.toHours()}, schedule=${schedule.name}")

        _events.emit(FastingEvent.ShowFastingComplete)
        _events.emit(FastingEvent.TriggerHapticFeedback)
    }

    fun startFasting() {
        viewModelScope.launch {
            val dataState = repository.fastingDataState.first()
            val targetHours = if (dataState.selectedSchedule == FastingSchedule.CUSTOM) {
                dataState.customFastingHours
            } else {
                dataState.selectedSchedule.fastingHours
            }

            val now = Instant.now()
            repository.saveFastingState(FastingState.FASTING, now)
            lastMilestoneHours = 0

            // Start foreground service
            FastingTimerService.startService(application, targetHours)

            // Update widget
            FastingWidgetProvider.updateAllWidgets(application)

            // Analytics: fast started
            Log.i(TAG, "fast_started: schedule=${dataState.selectedSchedule.name}, target_hours=$targetHours")
        }
    }

    fun stopFasting(saveSession: Boolean) {
        viewModelScope.launch {
            if (saveSession) {
                val dataState = repository.fastingDataState.first()
                val startTime = dataState.fastingStartTime
                if (startTime != null) {
                    val now = Instant.now()
                    val targetHours = if (dataState.selectedSchedule == FastingSchedule.CUSTOM) {
                        dataState.customFastingHours
                    } else {
                        dataState.selectedSchedule.fastingHours
                    }

                    val session = FastingSession(
                        id = UUID.randomUUID().toString(),
                        startTime = startTime,
                        endTime = now,
                        targetDuration = Duration.ofHours(targetHours.toLong()),
                        schedule = dataState.selectedSchedule,
                        completed = false // Stopped early
                    )
                    repository.saveSession(session)
                }
            }

            repository.saveFastingState(FastingState.NOT_STARTED, null)
            repository.saveEatingWindowStartTime(null)
            FastingTimerService.stopService(application)
            lastMilestoneHours = 0

            // Update widget
            FastingWidgetProvider.updateAllWidgets(application)

            // Analytics: fast stopped early
            Log.i(TAG, "fast_stopped_early: saved=$saveSession")
        }
    }

    fun endEatingWindow() {
        viewModelScope.launch {
            repository.saveFastingState(FastingState.NOT_STARTED, null)
            repository.saveEatingWindowStartTime(null)

            // Update widget
            FastingWidgetProvider.updateAllWidgets(application)
        }
    }

    fun selectSchedule(schedule: FastingSchedule) {
        viewModelScope.launch {
            repository.saveSelectedSchedule(schedule)
        }
    }

    fun setCustomFastingHours(hours: Int) {
        viewModelScope.launch {
            repository.saveCustomFastingHours(hours)
        }
    }

    fun incrementWater() {
        viewModelScope.launch {
            repository.incrementWater()
            // Analytics: water logged
            Log.i(TAG, "water_logged: action=increment")
        }
    }

    fun decrementWater() {
        viewModelScope.launch {
            repository.decrementWater()
            // Analytics: water logged
            Log.i(TAG, "water_logged: action=decrement")
        }
    }

    fun navigateToHistory() {
        viewModelScope.launch {
            _events.emit(FastingEvent.NavigateToHistory)
        }
    }

    fun navigateToSettings() {
        viewModelScope.launch {
            _events.emit(FastingEvent.NavigateToSettings)
        }
    }

    fun onPermissionDenied() {
        viewModelScope.launch {
            _events.emit(FastingEvent.ShowError("Notification permission required for fasting alerts"))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

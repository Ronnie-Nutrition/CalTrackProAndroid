package com.easyaiflows.caltrackpro.ui.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FastingRepository
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FastingSettingsUiState(
    val isLoading: Boolean = true,
    val selectedSchedule: FastingSchedule = FastingSchedule.default(),
    val customFastingHours: Int = 16,
    val waterGoalGlasses: Int = 8,
    val remindersEnabled: Boolean = true,
    val fastingCompleteNotification: Boolean = true,
    val milestoneNotifications: Boolean = true,
    val waterReminderNotifications: Boolean = true,
    val eatingWindowNotifications: Boolean = true
)

@HiltViewModel
class FastingSettingsViewModel @Inject constructor(
    private val repository: FastingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FastingSettingsUiState())
    val uiState: StateFlow<FastingSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            repository.fastingDataState.collect { dataState ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    selectedSchedule = dataState.selectedSchedule,
                    customFastingHours = dataState.customFastingHours,
                    waterGoalGlasses = dataState.waterGoalGlasses,
                    remindersEnabled = dataState.remindersEnabled
                )
            }
        }
    }

    fun setSelectedSchedule(schedule: FastingSchedule) {
        viewModelScope.launch {
            repository.saveSelectedSchedule(schedule)
            _uiState.value = _uiState.value.copy(selectedSchedule = schedule)
        }
    }

    fun setCustomFastingHours(hours: Int) {
        val validHours = hours.coerceIn(1, 23)
        viewModelScope.launch {
            repository.saveCustomFastingHours(validHours)
            _uiState.value = _uiState.value.copy(customFastingHours = validHours)
        }
    }

    fun setWaterGoalGlasses(glasses: Int) {
        val validGlasses = glasses.coerceIn(1, 20)
        viewModelScope.launch {
            repository.saveWaterGoal(validGlasses)
            _uiState.value = _uiState.value.copy(waterGoalGlasses = validGlasses)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveRemindersEnabled(enabled)
            _uiState.value = _uiState.value.copy(remindersEnabled = enabled)
        }
    }

    fun setFastingCompleteNotification(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(fastingCompleteNotification = enabled)
        // Note: Individual notification toggles would need additional DataStore keys
        // For now, these are UI-only until fully implemented
    }

    fun setMilestoneNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(milestoneNotifications = enabled)
    }

    fun setWaterReminderNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(waterReminderNotifications = enabled)
    }

    fun setEatingWindowNotifications(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(eatingWindowNotifications = enabled)
    }
}

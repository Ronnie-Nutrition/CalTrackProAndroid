package com.easyaiflows.caltrackpro.ui.fasting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FastingRepository
import com.easyaiflows.caltrackpro.domain.model.FastingSession
import com.easyaiflows.caltrackpro.domain.model.FastingStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

data class FastingHistoryUiState(
    val isLoading: Boolean = true,
    val sessions: List<FastingSession> = emptyList(),
    val stats: FastingStats = FastingStats(),
    val selectedMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val sessionsForSelectedDate: List<FastingSession> = emptyList(),
    val isCalendarView: Boolean = true
)

@HiltViewModel
class FastingHistoryViewModel @Inject constructor(
    private val repository: FastingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FastingHistoryUiState())
    val uiState: StateFlow<FastingHistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val sessions = repository.getAllSessions()
            val stats = repository.stats.first()

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                sessions = sessions.sortedByDescending { it.startTime },
                stats = stats
            )
        }
    }

    fun selectMonth(yearMonth: YearMonth) {
        _uiState.value = _uiState.value.copy(
            selectedMonth = yearMonth,
            selectedDate = null,
            sessionsForSelectedDate = emptyList()
        )
    }

    fun previousMonth() {
        val newMonth = _uiState.value.selectedMonth.minusMonths(1)
        selectMonth(newMonth)
    }

    fun nextMonth() {
        val newMonth = _uiState.value.selectedMonth.plusMonths(1)
        selectMonth(newMonth)
    }

    fun selectDate(date: LocalDate) {
        val sessionsForDate = _uiState.value.sessions.filter { session ->
            val sessionDate = session.startTime.atZone(ZoneId.systemDefault()).toLocalDate()
            sessionDate == date
        }

        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            sessionsForSelectedDate = sessionsForDate
        )
    }

    fun clearDateSelection() {
        _uiState.value = _uiState.value.copy(
            selectedDate = null,
            sessionsForSelectedDate = emptyList()
        )
    }

    fun toggleViewMode() {
        _uiState.value = _uiState.value.copy(
            isCalendarView = !_uiState.value.isCalendarView
        )
    }

    fun getSessionsForDate(date: LocalDate): List<FastingSession> {
        return _uiState.value.sessions.filter { session ->
            val sessionDate = session.startTime.atZone(ZoneId.systemDefault()).toLocalDate()
            sessionDate == date
        }
    }

    fun hasSessionOnDate(date: LocalDate): SessionStatus {
        val sessions = getSessionsForDate(date)
        if (sessions.isEmpty()) return SessionStatus.NONE

        val hasCompleted = sessions.any { it.completed }
        val hasPartial = sessions.any { !it.completed }

        return when {
            hasCompleted -> SessionStatus.COMPLETED
            hasPartial -> SessionStatus.PARTIAL
            else -> SessionStatus.NONE
        }
    }
}

enum class SessionStatus {
    NONE, PARTIAL, COMPLETED
}

package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.FastingDataState
import com.easyaiflows.caltrackpro.domain.model.FastingPreferences
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingSession
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.domain.model.FastingStats
import kotlinx.coroutines.flow.Flow
import java.time.Instant

/**
 * Repository interface for fasting operations.
 * Uses DataStore for persistence.
 */
interface FastingRepository {

    /**
     * Observe the current fasting data state.
     */
    val fastingDataState: Flow<FastingDataState>

    /**
     * Observe fasting preferences.
     */
    val preferences: Flow<FastingPreferences>

    /**
     * Observe fasting statistics.
     */
    val stats: Flow<FastingStats>

    /**
     * Save the current fasting state and start time.
     */
    suspend fun saveFastingState(state: FastingState, startTime: Instant?)

    /**
     * Save eating window start time.
     */
    suspend fun saveEatingWindowStartTime(startTime: Instant?)

    /**
     * Save the selected fasting schedule.
     */
    suspend fun saveSelectedSchedule(schedule: FastingSchedule)

    /**
     * Save custom fasting hours (for CUSTOM schedule).
     */
    suspend fun saveCustomFastingHours(hours: Int)

    /**
     * Save a completed fasting session.
     */
    suspend fun saveSession(session: FastingSession)

    /**
     * Get fasting sessions from the last N days.
     */
    suspend fun getSessions(lastNDays: Int): List<FastingSession>

    /**
     * Get all fasting sessions.
     */
    suspend fun getAllSessions(): List<FastingSession>

    /**
     * Increment water intake by one glass.
     */
    suspend fun incrementWater()

    /**
     * Decrement water intake by one glass.
     */
    suspend fun decrementWater()

    /**
     * Reset water intake if it's a new day.
     */
    suspend fun resetWaterIfNewDay()

    /**
     * Save water goal (glasses per day).
     */
    suspend fun saveWaterGoal(glasses: Int)

    /**
     * Save reminders enabled setting.
     */
    suspend fun saveRemindersEnabled(enabled: Boolean)

    /**
     * Clear all fasting data.
     */
    suspend fun clearAll()
}

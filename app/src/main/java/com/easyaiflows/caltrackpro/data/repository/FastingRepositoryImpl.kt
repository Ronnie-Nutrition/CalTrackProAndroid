package com.easyaiflows.caltrackpro.data.repository

import com.easyaiflows.caltrackpro.data.local.FastingDataState
import com.easyaiflows.caltrackpro.data.local.FastingDataStore
import com.easyaiflows.caltrackpro.domain.model.FastingPreferences
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingSession
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.domain.model.FastingStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastingRepositoryImpl @Inject constructor(
    private val fastingDataStore: FastingDataStore
) : FastingRepository {

    override val fastingDataState: Flow<FastingDataState> = fastingDataStore.fastingDataState

    override val preferences: Flow<FastingPreferences> = fastingDataStore.fastingDataState.map { state ->
        FastingPreferences(
            selectedSchedule = state.selectedSchedule,
            customFastingHours = state.customFastingHours,
            waterGoalGlasses = state.waterGoalGlasses,
            remindersEnabled = state.remindersEnabled
        )
    }

    override val stats: Flow<FastingStats> = fastingDataStore.sessionsJson.map { json ->
        val sessions = parseSessionsFromJson(json)
        calculateStats(sessions)
    }

    override suspend fun saveFastingState(state: FastingState, startTime: Instant?) {
        fastingDataStore.saveFastingState(state, startTime)
    }

    override suspend fun saveEatingWindowStartTime(startTime: Instant?) {
        fastingDataStore.saveEatingWindowStartTime(startTime)
    }

    override suspend fun saveSelectedSchedule(schedule: FastingSchedule) {
        fastingDataStore.saveSelectedSchedule(schedule)
    }

    override suspend fun saveCustomFastingHours(hours: Int) {
        fastingDataStore.saveCustomFastingHours(hours)
    }

    override suspend fun saveSession(session: FastingSession) {
        val currentJson = fastingDataStore.sessionsJson.first()
        val sessions = parseSessionsFromJson(currentJson).toMutableList()
        sessions.add(session)
        val newJson = sessionsToJson(sessions)
        fastingDataStore.saveSessionsJson(newJson)
    }

    override suspend fun getSessions(lastNDays: Int): List<FastingSession> {
        val json = fastingDataStore.sessionsJson.first()
        val sessions = parseSessionsFromJson(json)
        val cutoff = Instant.now().minus(lastNDays.toLong(), ChronoUnit.DAYS)
        return sessions.filter { it.startTime.isAfter(cutoff) }
    }

    override suspend fun getAllSessions(): List<FastingSession> {
        val json = fastingDataStore.sessionsJson.first()
        return parseSessionsFromJson(json)
    }

    override suspend fun incrementWater() {
        fastingDataStore.incrementWater()
    }

    override suspend fun decrementWater() {
        fastingDataStore.decrementWater()
    }

    override suspend fun resetWaterIfNewDay() {
        fastingDataStore.resetWaterIfNewDay()
    }

    override suspend fun saveWaterGoal(glasses: Int) {
        fastingDataStore.saveWaterGoal(glasses)
    }

    override suspend fun saveRemindersEnabled(enabled: Boolean) {
        fastingDataStore.saveRemindersEnabled(enabled)
    }

    override suspend fun clearAll() {
        fastingDataStore.clearAll()
    }

    // JSON serialization helpers

    private fun parseSessionsFromJson(json: String?): List<FastingSession> {
        if (json.isNullOrEmpty()) return emptyList()

        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).map { index ->
                val obj = jsonArray.getJSONObject(index)
                FastingSession(
                    id = obj.getString("id"),
                    startTime = Instant.ofEpochMilli(obj.getLong("startTime")),
                    endTime = obj.optLong("endTime", -1).takeIf { it != -1L }?.let { Instant.ofEpochMilli(it) },
                    targetDuration = Duration.ofMillis(obj.getLong("targetDuration")),
                    schedule = FastingSchedule.valueOf(obj.getString("schedule")),
                    completed = obj.getBoolean("completed")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun sessionsToJson(sessions: List<FastingSession>): String {
        val jsonArray = JSONArray()
        sessions.forEach { session ->
            val obj = JSONObject().apply {
                put("id", session.id)
                put("startTime", session.startTime.toEpochMilli())
                session.endTime?.let { put("endTime", it.toEpochMilli()) }
                put("targetDuration", session.targetDuration.toMillis())
                put("schedule", session.schedule.name)
                put("completed", session.completed)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    private fun calculateStats(sessions: List<FastingSession>): FastingStats {
        val completedSessions = sessions.filter { it.completed }.sortedByDescending { it.startTime }

        if (completedSessions.isEmpty()) {
            return FastingStats()
        }

        // Calculate streaks
        val currentStreak = calculateCurrentStreak(completedSessions)
        val longestStreak = calculateLongestStreak(completedSessions)

        // Calculate fasts this week
        val weekStart = LocalDate.now().minusDays(7)
        val weekStartInstant = weekStart.atStartOfDay().toInstant(java.time.ZoneOffset.UTC)
        val fastsThisWeek = completedSessions.count { it.startTime.isAfter(weekStartInstant) }

        return FastingStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalFastsCompleted = completedSessions.size,
            fastsThisWeek = fastsThisWeek
        )
    }

    private fun calculateCurrentStreak(completedSessions: List<FastingSession>): Int {
        if (completedSessions.isEmpty()) return 0

        var streak = 0
        var currentDate = LocalDate.now()

        for (session in completedSessions) {
            val sessionDate = session.startTime.atZone(java.time.ZoneId.systemDefault()).toLocalDate()

            when {
                sessionDate == currentDate -> {
                    streak++
                    currentDate = currentDate.minusDays(1)
                }
                sessionDate == currentDate.minusDays(1) -> {
                    streak++
                    currentDate = sessionDate.minusDays(1)
                }
                else -> break
            }
        }

        return streak
    }

    private fun calculateLongestStreak(completedSessions: List<FastingSession>): Int {
        if (completedSessions.isEmpty()) return 0

        val sortedSessions = completedSessions.sortedBy { it.startTime }
        var longestStreak = 1
        var currentStreak = 1
        var previousDate: LocalDate? = null

        for (session in sortedSessions) {
            val sessionDate = session.startTime.atZone(java.time.ZoneId.systemDefault()).toLocalDate()

            if (previousDate != null) {
                val daysBetween = ChronoUnit.DAYS.between(previousDate, sessionDate)
                if (daysBetween <= 1) {
                    currentStreak++
                    longestStreak = maxOf(longestStreak, currentStreak)
                } else {
                    currentStreak = 1
                }
            }
            previousDate = sessionDate
        }

        return longestStreak
    }
}

package com.easyaiflows.caltrackpro.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

private val Context.fastingDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "fasting_preferences"
)

/**
 * Data class representing the current fasting state from DataStore.
 */
data class FastingDataState(
    val currentState: FastingState = FastingState.NOT_STARTED,
    val fastingStartTime: Instant? = null,
    val eatingWindowStartTime: Instant? = null,
    val selectedSchedule: FastingSchedule = FastingSchedule.default(),
    val customFastingHours: Int = 16,
    val waterIntake: Int = 0,
    val lastWaterResetDate: LocalDate = LocalDate.now(),
    val waterGoalGlasses: Int = 8,
    val remindersEnabled: Boolean = true
)

@Singleton
class FastingDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore: DataStore<Preferences>
        get() = context.fastingDataStore

    companion object {
        // Fasting state keys
        private val KEY_CURRENT_STATE = stringPreferencesKey("fasting_current_state")
        private val KEY_FASTING_START_TIME = longPreferencesKey("fasting_start_time")
        private val KEY_EATING_WINDOW_START_TIME = longPreferencesKey("eating_window_start_time")

        // Schedule keys
        private val KEY_SELECTED_SCHEDULE = stringPreferencesKey("fasting_selected_schedule")
        private val KEY_CUSTOM_FASTING_HOURS = intPreferencesKey("fasting_custom_hours")

        // Water tracking keys
        private val KEY_WATER_INTAKE = intPreferencesKey("fasting_water_intake")
        private val KEY_LAST_WATER_RESET_DATE = stringPreferencesKey("fasting_last_water_reset_date")
        private val KEY_WATER_GOAL_GLASSES = intPreferencesKey("fasting_water_goal_glasses")

        // Notification keys
        private val KEY_REMINDERS_ENABLED = booleanPreferencesKey("fasting_reminders_enabled")

        // Session history keys (stored as JSON strings)
        private val KEY_SESSIONS_JSON = stringPreferencesKey("fasting_sessions_json")
    }

    /**
     * Flow of the current fasting state data.
     */
    val fastingDataState: Flow<FastingDataState> = dataStore.data.map { preferences ->
        FastingDataState(
            currentState = FastingState.valueOf(
                preferences[KEY_CURRENT_STATE] ?: FastingState.NOT_STARTED.name
            ),
            fastingStartTime = preferences[KEY_FASTING_START_TIME]?.let { Instant.ofEpochMilli(it) },
            eatingWindowStartTime = preferences[KEY_EATING_WINDOW_START_TIME]?.let { Instant.ofEpochMilli(it) },
            selectedSchedule = FastingSchedule.valueOf(
                preferences[KEY_SELECTED_SCHEDULE] ?: FastingSchedule.default().name
            ),
            customFastingHours = preferences[KEY_CUSTOM_FASTING_HOURS] ?: 16,
            waterIntake = preferences[KEY_WATER_INTAKE] ?: 0,
            lastWaterResetDate = preferences[KEY_LAST_WATER_RESET_DATE]?.let {
                LocalDate.parse(it)
            } ?: LocalDate.now(),
            waterGoalGlasses = preferences[KEY_WATER_GOAL_GLASSES] ?: 8,
            remindersEnabled = preferences[KEY_REMINDERS_ENABLED] ?: true
        )
    }

    /**
     * Flow of session history as JSON string.
     */
    val sessionsJson: Flow<String?> = dataStore.data.map { preferences ->
        preferences[KEY_SESSIONS_JSON]
    }

    /**
     * Save the current fasting state and start time.
     */
    suspend fun saveFastingState(state: FastingState, startTime: Instant?) {
        dataStore.edit { preferences ->
            preferences[KEY_CURRENT_STATE] = state.name
            if (startTime != null) {
                preferences[KEY_FASTING_START_TIME] = startTime.toEpochMilli()
            } else {
                preferences.remove(KEY_FASTING_START_TIME)
            }
        }
    }

    /**
     * Save eating window start time.
     */
    suspend fun saveEatingWindowStartTime(startTime: Instant?) {
        dataStore.edit { preferences ->
            if (startTime != null) {
                preferences[KEY_EATING_WINDOW_START_TIME] = startTime.toEpochMilli()
            } else {
                preferences.remove(KEY_EATING_WINDOW_START_TIME)
            }
        }
    }

    /**
     * Save the selected fasting schedule.
     */
    suspend fun saveSelectedSchedule(schedule: FastingSchedule) {
        dataStore.edit { preferences ->
            preferences[KEY_SELECTED_SCHEDULE] = schedule.name
        }
    }

    /**
     * Save custom fasting hours (for CUSTOM schedule).
     */
    suspend fun saveCustomFastingHours(hours: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_CUSTOM_FASTING_HOURS] = hours.coerceIn(1, 23)
        }
    }

    /**
     * Increment water intake by one glass.
     */
    suspend fun incrementWater() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_WATER_INTAKE] ?: 0
            preferences[KEY_WATER_INTAKE] = current + 1
        }
    }

    /**
     * Decrement water intake by one glass (minimum 0).
     */
    suspend fun decrementWater() {
        dataStore.edit { preferences ->
            val current = preferences[KEY_WATER_INTAKE] ?: 0
            preferences[KEY_WATER_INTAKE] = (current - 1).coerceAtLeast(0)
        }
    }

    /**
     * Reset water intake if it's a new day.
     */
    suspend fun resetWaterIfNewDay() {
        dataStore.edit { preferences ->
            val lastResetStr = preferences[KEY_LAST_WATER_RESET_DATE]
            val lastReset = lastResetStr?.let { LocalDate.parse(it) } ?: LocalDate.MIN
            val today = LocalDate.now()

            if (lastReset.isBefore(today)) {
                preferences[KEY_WATER_INTAKE] = 0
                preferences[KEY_LAST_WATER_RESET_DATE] = today.toString()
            }
        }
    }

    /**
     * Save water goal (glasses per day).
     */
    suspend fun saveWaterGoal(glasses: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_WATER_GOAL_GLASSES] = glasses.coerceIn(1, 20)
        }
    }

    /**
     * Save reminders enabled setting.
     */
    suspend fun saveRemindersEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[KEY_REMINDERS_ENABLED] = enabled
        }
    }

    /**
     * Save session history as JSON string.
     */
    suspend fun saveSessionsJson(json: String) {
        dataStore.edit { preferences ->
            preferences[KEY_SESSIONS_JSON] = json
        }
    }

    /**
     * Clear all fasting data.
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

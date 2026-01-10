package com.easyaiflows.caltrackpro.ui.fasting

import com.easyaiflows.caltrackpro.domain.model.FastingMilestone
import com.easyaiflows.caltrackpro.domain.model.FastingSchedule
import com.easyaiflows.caltrackpro.domain.model.FastingState
import com.easyaiflows.caltrackpro.domain.model.FastingStats
import java.time.Duration
import java.time.Instant

/**
 * Sealed class representing different UI states for the Fasting screen.
 */
sealed class FastingUiState {
    /**
     * Loading state while fetching fasting data.
     */
    data object Loading : FastingUiState()

    /**
     * Not started state - user hasn't begun fasting.
     */
    data class NotStarted(
        val selectedSchedule: FastingSchedule = FastingSchedule.default(),
        val customFastingHours: Int = 16,
        val stats: FastingStats = FastingStats(),
        val waterGoalGlasses: Int = 8
    ) : FastingUiState()

    /**
     * Fasting state - user is currently fasting.
     */
    data class Fasting(
        val startTime: Instant,
        val targetDuration: Duration,
        val schedule: FastingSchedule,
        val elapsedDuration: Duration = Duration.ZERO,
        val progress: Float = 0f,
        val currentMilestone: FastingMilestone,
        val nextMilestone: FastingMilestone?,
        val waterIntake: Int = 0,
        val waterGoalGlasses: Int = 8,
        val stats: FastingStats = FastingStats()
    ) : FastingUiState() {
        val remainingDuration: Duration
            get() {
                val remaining = targetDuration.minus(elapsedDuration)
                return if (remaining.isNegative) Duration.ZERO else remaining
            }

        val elapsedHours: Int
            get() = elapsedDuration.toHours().toInt()

        val elapsedMinutes: Int
            get() = (elapsedDuration.toMinutes() % 60).toInt()

        val elapsedSeconds: Int
            get() = (elapsedDuration.seconds % 60).toInt()

        val remainingHours: Int
            get() = remainingDuration.toHours().toInt()

        val remainingMinutes: Int
            get() = (remainingDuration.toMinutes() % 60).toInt()

        val isGoalReached: Boolean
            get() = elapsedDuration >= targetDuration

        val hoursUntilNextMilestone: Int?
            get() = nextMilestone?.let { it.hours - elapsedHours }
    }

    /**
     * Eating state - user is in eating window after completing fast.
     */
    data class Eating(
        val fastingEndTime: Instant,
        val eatingWindowStartTime: Instant,
        val eatingWindowDuration: Duration,
        val completedFastDuration: Duration,
        val schedule: FastingSchedule,
        val elapsedEatingDuration: Duration = Duration.ZERO,
        val waterIntake: Int = 0,
        val waterGoalGlasses: Int = 8,
        val stats: FastingStats = FastingStats()
    ) : FastingUiState() {
        val remainingEatingDuration: Duration
            get() {
                val remaining = eatingWindowDuration.minus(elapsedEatingDuration)
                return if (remaining.isNegative) Duration.ZERO else remaining
            }

        val eatingWindowProgress: Float
            get() {
                if (eatingWindowDuration.isZero) return 0f
                return (elapsedEatingDuration.toMillis().toFloat() / eatingWindowDuration.toMillis()).coerceIn(0f, 1f)
            }

        val remainingHours: Int
            get() = remainingEatingDuration.toHours().toInt()

        val remainingMinutes: Int
            get() = (remainingEatingDuration.toMinutes() % 60).toInt()

        val isEatingWindowClosed: Boolean
            get() = elapsedEatingDuration >= eatingWindowDuration
    }

    /**
     * Error state with message.
     */
    data class Error(
        val message: String,
        val previousState: FastingState = FastingState.NOT_STARTED
    ) : FastingUiState()
}

package com.easyaiflows.caltrackpro.domain.model

import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * Represents a single fasting session with start/end times and completion status.
 */
data class FastingSession(
    val id: String = UUID.randomUUID().toString(),
    val startTime: Instant,
    val endTime: Instant? = null,
    val targetDuration: Duration,
    val schedule: FastingSchedule,
    val completed: Boolean = false
) {
    /**
     * The actual duration of the fasting session.
     * If the session is still active (endTime is null), calculates from now.
     */
    val actualDuration: Duration
        get() = Duration.between(startTime, endTime ?: Instant.now())

    /**
     * Progress as a value from 0.0 to 1.0.
     * Returns 1.0 if target duration is reached or exceeded.
     */
    val progress: Float
        get() {
            if (targetDuration.isZero) return 0f
            val progressRatio = actualDuration.toMillis().toFloat() / targetDuration.toMillis()
            return progressRatio.coerceIn(0f, 1f)
        }

    /**
     * Remaining duration until target is reached.
     * Returns Duration.ZERO if target is already reached.
     */
    val remainingDuration: Duration
        get() {
            val remaining = targetDuration.minus(actualDuration)
            return if (remaining.isNegative) Duration.ZERO else remaining
        }

    /**
     * Whether the fasting goal has been reached.
     */
    val isGoalReached: Boolean
        get() = actualDuration >= targetDuration
}

package com.easyaiflows.caltrackpro.domain.model

/**
 * Data class representing fasting statistics for a user.
 */
data class FastingStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalFastsCompleted: Int = 0,
    val fastsThisWeek: Int = 0
)

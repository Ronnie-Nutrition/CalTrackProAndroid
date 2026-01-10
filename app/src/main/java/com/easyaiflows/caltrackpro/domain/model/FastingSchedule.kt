package com.easyaiflows.caltrackpro.domain.model

/**
 * Represents the different intermittent fasting protocols.
 * Each schedule defines the fasting duration and eating window.
 */
enum class FastingSchedule(
    val displayName: String,
    val fastingHours: Int,
    val eatingHours: Int
) {
    SCHEDULE_16_8("16:8", 16, 8),
    SCHEDULE_18_6("18:6", 18, 6),
    SCHEDULE_20_4("20:4", 20, 4),
    SCHEDULE_OMAD("OMAD (23:1)", 23, 1),
    CUSTOM("Custom", 0, 0);

    companion object {
        /**
         * Returns the default fasting schedule.
         */
        fun default(): FastingSchedule = SCHEDULE_16_8
    }
}

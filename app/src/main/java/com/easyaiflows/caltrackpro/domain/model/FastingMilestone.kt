package com.easyaiflows.caltrackpro.domain.model

import androidx.annotation.DrawableRes
import com.easyaiflows.caltrackpro.R

/**
 * Represents a health benefit milestone reached during fasting.
 */
data class FastingMilestone(
    val hours: Int,
    val title: String,
    val description: String,
    @DrawableRes val iconResId: Int
)

/**
 * The 8 fasting benefit milestones with their health benefits.
 * Based on scientific research about what happens in the body during fasting.
 */
val FASTING_MILESTONES = listOf(
    FastingMilestone(
        hours = 0,
        title = "Fast Started",
        description = "Body transitions from fed to fasting state",
        iconResId = R.drawable.ic_play_circle
    ),
    FastingMilestone(
        hours = 4,
        title = "Insulin Drops",
        description = "Blood sugar stabilizes, insulin levels decrease",
        iconResId = R.drawable.ic_arrow_down_circle
    ),
    FastingMilestone(
        hours = 8,
        title = "Glucose Used",
        description = "Body depletes glycogen (glucose) stores",
        iconResId = R.drawable.ic_flame
    ),
    FastingMilestone(
        hours = 12,
        title = "Fat Burning",
        description = "Ketosis begins, body burns fat for fuel",
        iconResId = R.drawable.ic_bolt
    ),
    FastingMilestone(
        hours = 14,
        title = "Growth Hormone",
        description = "HGH levels increase, supporting muscle preservation",
        iconResId = R.drawable.ic_arrow_up_circle
    ),
    FastingMilestone(
        hours = 16,
        title = "Autophagy",
        description = "Cellular cleanup process begins",
        iconResId = R.drawable.ic_sparkles
    ),
    FastingMilestone(
        hours = 18,
        title = "Deep Ketosis",
        description = "Maximum fat-burning efficiency achieved",
        iconResId = R.drawable.ic_flame_circle
    ),
    FastingMilestone(
        hours = 24,
        title = "Cell Regeneration",
        description = "Enhanced autophagy, cellular repair and renewal",
        iconResId = R.drawable.ic_refresh
    )
)

/**
 * Helper function to get the current milestone based on elapsed hours.
 */
fun getCurrentMilestone(elapsedHours: Int): FastingMilestone {
    return FASTING_MILESTONES.lastOrNull { it.hours <= elapsedHours } ?: FASTING_MILESTONES.first()
}

/**
 * Helper function to get the next milestone based on elapsed hours.
 * Returns null if all milestones have been reached.
 */
fun getNextMilestone(elapsedHours: Int): FastingMilestone? {
    return FASTING_MILESTONES.firstOrNull { it.hours > elapsedHours }
}

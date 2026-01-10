package com.easyaiflows.caltrackpro.ui.fasting

/**
 * Sealed class representing one-time events from the Fasting screen.
 */
sealed class FastingEvent {
    data object NavigateToHistory : FastingEvent()
    data object NavigateToSettings : FastingEvent()
    data class ShowMilestoneReached(val milestoneTitle: String) : FastingEvent()
    data object ShowFastingComplete : FastingEvent()
    data object ShowEatingWindowWarning : FastingEvent()
    data object ShowEatingWindowClosed : FastingEvent()
    data class ShowError(val message: String) : FastingEvent()
    data object TriggerHapticFeedback : FastingEvent()
}

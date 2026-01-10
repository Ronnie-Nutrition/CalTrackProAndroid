package com.easyaiflows.caltrackpro.domain.model

/**
 * Represents the current state of a fasting session.
 */
enum class FastingState(val displayName: String) {
    NOT_STARTED("Not Started"),
    FASTING("Fasting"),
    EATING("Eating Window")
}

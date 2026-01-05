package com.easyaiflows.caltrackpro.domain.model

/**
 * Biological sex used for BMR calculation.
 * The Mifflin-St Jeor formula uses different constants for male and female.
 */
enum class Sex {
    MALE,
    FEMALE;

    companion object {
        /**
         * Default sex for new profiles
         */
        val Default = MALE

        /**
         * Get Sex from string, with fallback to default
         */
        fun fromString(value: String?): Sex {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: Default
        }
    }
}

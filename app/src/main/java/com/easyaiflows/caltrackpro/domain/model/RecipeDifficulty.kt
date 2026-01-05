package com.easyaiflows.caltrackpro.domain.model

/**
 * Represents the difficulty level of a recipe.
 */
enum class RecipeDifficulty(val displayName: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard");

    companion object {
        fun fromString(value: String): RecipeDifficulty {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: EASY
        }
    }
}

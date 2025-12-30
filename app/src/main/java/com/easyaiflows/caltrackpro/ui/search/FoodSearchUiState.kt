package com.easyaiflows.caltrackpro.ui.search

import com.easyaiflows.caltrackpro.domain.model.SearchedFood

/**
 * Tab options for the Food Search screen
 */
enum class SearchTab {
    SEARCH,
    RECENT,
    FAVORITES
}

/**
 * UI state for the Food Search screen
 */
data class FoodSearchUiState(
    val query: String = "",
    val results: List<SearchedFood> = emptyList(),
    val recentSearches: List<SearchedFood> = emptyList(),
    val favorites: List<SearchedFood> = emptyList(),
    val selectedTab: SearchTab = SearchTab.SEARCH,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
) {
    /**
     * Check if results are empty after a search
     */
    val isEmpty: Boolean
        get() = hasSearched && results.isEmpty() && !isLoading && error == null

    /**
     * Check if in initial state (no search performed yet)
     */
    val isInitial: Boolean
        get() = !hasSearched && results.isEmpty() && !isLoading && error == null

    /**
     * Check if showing results
     */
    val hasResults: Boolean
        get() = results.isNotEmpty()

    /**
     * Get the list to display based on selected tab
     */
    val displayList: List<SearchedFood>
        get() = when (selectedTab) {
            SearchTab.SEARCH -> results
            SearchTab.RECENT -> recentSearches
            SearchTab.FAVORITES -> favorites
        }
}

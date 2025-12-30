package com.easyaiflows.caltrackpro.ui.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyaiflows.caltrackpro.data.repository.FoodSearchRepository
import com.easyaiflows.caltrackpro.domain.model.MealType
import com.easyaiflows.caltrackpro.domain.model.SearchedFood
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class FoodSearchViewModel @Inject constructor(
    private val repository: FoodSearchRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val DEBOUNCE_DELAY_MS = 300L
        private const val MIN_QUERY_LENGTH = 2
    }

    // Navigation parameters
    val mealType: MealType = savedStateHandle.get<String>("mealType")
        ?.let { MealType.valueOf(it) }
        ?: MealType.SNACK

    val date: LocalDate = savedStateHandle.get<String>("date")
        ?.let { LocalDate.parse(it) }
        ?: LocalDate.now()

    private val _query = MutableStateFlow("")
    private val _selectedTab = MutableStateFlow(SearchTab.SEARCH)
    private val _searchState = MutableStateFlow(SearchState())

    private data class SearchState(
        val results: List<SearchedFood> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val hasSearched: Boolean = false
    )

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private val searchResults = _query
        .debounce(DEBOUNCE_DELAY_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            flow {
                if (query.length < MIN_QUERY_LENGTH) {
                    emit(SearchState(hasSearched = false))
                } else {
                    emit(SearchState(isLoading = true))

                    val result = repository.searchFoods(query)

                    result.fold(
                        onSuccess = { foods ->
                            emit(SearchState(
                                results = foods,
                                isLoading = false,
                                hasSearched = true
                            ))
                        },
                        onFailure = { error ->
                            emit(SearchState(
                                isLoading = false,
                                error = error.message ?: "Search failed",
                                hasSearched = true
                            ))
                        }
                    )
                }
            }
        }

    val uiState: StateFlow<FoodSearchUiState> = combine(
        _query,
        _selectedTab,
        searchResults,
        repository.getRecentSearches(),
        repository.getFavorites()
    ) { query, tab, searchState, recentSearches, favorites ->
        FoodSearchUiState(
            query = query,
            results = searchState.results,
            recentSearches = recentSearches,
            favorites = favorites,
            selectedTab = tab,
            isLoading = searchState.isLoading,
            error = searchState.error,
            hasSearched = searchState.hasSearched
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FoodSearchUiState()
    )

    /**
     * Update the search query.
     * Debounced search is triggered automatically.
     */
    fun updateQuery(query: String) {
        _query.value = query
        // Switch to search tab when typing
        if (query.isNotEmpty()) {
            _selectedTab.value = SearchTab.SEARCH
        }
    }

    /**
     * Clear the search query and results.
     */
    fun clearSearch() {
        _query.value = ""
    }

    /**
     * Select a tab.
     */
    fun selectTab(tab: SearchTab) {
        _selectedTab.value = tab
    }

    /**
     * Toggle favorite status for a food.
     */
    fun toggleFavorite(food: SearchedFood) {
        viewModelScope.launch {
            repository.toggleFavorite(food)
        }
    }

    /**
     * Add a food to recent searches when selected.
     */
    fun onFoodSelected(food: SearchedFood) {
        viewModelScope.launch {
            repository.addToRecentSearches(food)
        }
    }

    /**
     * Clear all recent searches.
     */
    fun clearRecentSearches() {
        viewModelScope.launch {
            repository.clearRecentSearches()
        }
    }

    /**
     * Retry the last search after an error.
     */
    fun retry() {
        val currentQuery = _query.value
        if (currentQuery.isNotBlank()) {
            // Force a re-emission by setting a slightly modified value then back
            _query.value = ""
            _query.value = currentQuery
        }
    }
}
